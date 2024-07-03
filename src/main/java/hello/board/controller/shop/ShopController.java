package hello.board.controller.shop;

import hello.board.domain.*;
import hello.board.service.member.MemberService;
import hello.board.service.shop.ShopService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//쇼핑몰 기능
@Slf4j
@Controller
@RequestMapping("/shop/")
@RequiredArgsConstructor
public class ShopController {

	private final ShopService shopService;
	private final MemberService memberService;

	@Value("${image.upload.dir}")  // 이미지 저장 경로 설정
	private String imageUploadDir;

	@GetMapping("home")
	public String shopHome(Model model, @RequestParam(defaultValue = "1") int page,
						   @RequestParam(defaultValue = "9" ) int maxResult,
						   @RequestParam(defaultValue = "BOOK",value = "category") String category) {

		long count = shopService.getProductCount(category);
		long totalPage = count%maxResult==0?count/maxResult:count/maxResult+1;
		if(count==0)
			totalPage=1;
		long firstPage = page%5==0?page-4:page/5*5+1;
		long lastPage = firstPage+4;
		if(lastPage>totalPage)
			lastPage=totalPage;
		model.addAttribute("category",category);
		model.addAttribute("count",count);
		model.addAttribute("totalPage",totalPage);
		model.addAttribute("firstPage",firstPage);
		model.addAttribute("lastPage",lastPage);
		model.addAttribute("page",page);
		model.addAttribute("maxResult",maxResult);
		model.addAttribute("productList", shopService.productList(page,maxResult,category));
		return "shop/product-list";
	}
	@GetMapping("product/reg")
	public String product_reg(Model model,HttpSession session) {
		if(session.getAttribute("isSeller")==null)
			return "shop/notSeller";
		if(!(boolean)session.getAttribute("isSeller"))
			return "shop/notSeller";
		model.addAttribute("product",new Product());
		return "shop/product-reg";
	}
	@GetMapping("/image/{fileName}")
	public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
		try {
			Path filePath = Paths.get(imageUploadDir, fileName);
			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists() && resource.isReadable()) {
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath)) // 파일 유형 헤더 추가
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
						.body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PostMapping("product/reg")
	public String product_reg(HttpSession session, @RequestParam MultipartFile file, Product product) throws IOException {
		if (session.getAttribute("isSeller") != null && (boolean) session.getAttribute("isSeller")) {
			try {
				log.info("file={}", file);
				if (file.isEmpty()) {
					return "shop/empty-file"; // 이미지 파일이 없는 경우 처리
				} else {
					String uuid = UUID.randomUUID().toString();
					String fileName = uuid + ".png";
					product.setImagePath(fileName);

					byte[] bytes = file.getBytes();
					Path path = Paths.get(imageUploadDir, fileName); // Docker 볼륨 경로 사용
					Files.createDirectories(path.getParent());       // 필요한 경우 상위 디렉토리 생성
					Files.write(path, bytes);

					product.setMemberId((Long) session.getAttribute("memberId"));
					shopService.productReg(product);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return "shop/empty-file";
			}
		} else {
			return "shop/notSeller";
		}
		return "redirect:/shop/home";
	}
	@GetMapping("product/{id}")
	public String product(@PathVariable("id")Long id,Model model) {
		Product product = shopService.getProduct(id);
		model.addAttribute("product",product);
		return "shop/product";
	}
	@GetMapping("product/update/{id}")
	public String productUpdate(@PathVariable("id") Long productId,HttpSession session,Model model) {
		Product product = shopService.getProduct(productId);
		if(!product.getMemberId().equals((Long)session.getAttribute("memberId")))
			return "shop/product-memberCheck-fail";
		model.addAttribute("product",product);
		return "shop/product-update";
	}
	@PostMapping("product/update/{id}")
	public String productUpdate(@PathVariable("id") Long productId,
								HttpSession session,Product product,
								@RequestParam("file") MultipartFile file,
								@RequestParam("category") String category) throws IOException {
		if(!(boolean)session.getAttribute("isSeller"))
			return "shop/notSeller";
		Product findProduct = shopService.getProduct(productId);
		if(file.getSize()!=0) {
			byte[] bytes = file.getBytes();
			Path path = Paths.get("./src/main/resources/static/shop/image/"+findProduct.getImagePath());
			Files.write(path, bytes);
		}
		shopService.productUpdate(productId,product);
		return "redirect:/shop/product/"+productId;
	}
	@GetMapping("product/delete/{id}")
	public String productDelete(@PathVariable("id") Long productId,HttpSession session) {
		Product product = shopService.getProduct(productId);
		if(!product.getMemberId().equals((Long)session.getAttribute("memberId")))
			return "shop/product-memberCheck-fail";
		shopService.deleteProduct(productId);
		return "redirect:/shop/home";
	}
	@PostMapping("cart/add")
	public String cartAdd(@RequestParam(defaultValue = "1") Long productId,@RequestParam int quantity,HttpSession session) {
		if(session.getAttribute("memberId")==null)
			return "board/login-check";

		Member member = memberService.info((Long)session.getAttribute("memberId"));
		Product product = shopService.getProduct(productId);
		shopService.addCart(member,product,quantity);
		return "shop/cart-add-success";
	}
	@GetMapping("myCart")
	public String myCart(Model model, HttpSession session) {
		Member member = memberService.info((Long)session.getAttribute("memberId"));
		model.addAttribute("myCart", shopService.myCart(member));
		return "shop/myCart";
	}
	@GetMapping("cart/delete/{id}")
	public String deleteCart(@PathVariable("id") Long cartId) {
		shopService.deleteCart(cartId);
		return "redirect:/shop/myCart";
	}
	@GetMapping("cart/order")
	public String cartOrder(HttpSession session) {
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		return "shop/cartOrder";
	}

	@PostMapping("cart/order")
	public String cartOrder(@RequestParam("address")String address,@RequestParam("phone")String phone,Model model,HttpSession session) {
		Long memberId = (Long)session.getAttribute("memberId");
		Member member = memberService.info(memberId);
		List<Cart> carts = member.getCart();
		List<Orders> orders = new ArrayList<>();
		int totalPrice = 0;
		for(Cart cart : carts) {
			Orders order = new Orders();
			order.setMemberId(memberId);
			order.setAddress(address);
			order.setPhone(phone);
			order.setProductId(cart.getProduct().getId());
			order.setProductName(cart.getProduct().getName());
			order.setQuantity(cart.getQuantity());
			order.setSellerId(cart.getProduct().getMemberId());
			order.setStatus(OrderStatus.PENDING);
			order.setTotalPrice(cart.getProduct().getPrice()*cart.getQuantity());
			totalPrice += order.getTotalPrice();
			orders.add(order);
		}
		model.addAttribute("totalPrice",totalPrice);
		session.setAttribute("orders", orders);
		session.setAttribute("cartOrder", true);
		return "shop/payment";
	}
	@GetMapping("payment/success")
	public String paymentsuccess(@RequestParam("payment")boolean payment,HttpSession session) {
		boolean cartOrder = (boolean)session.getAttribute("cartOrder");
		if(payment) {
			List<Orders> orders = (List<Orders>)session.getAttribute("orders");
			for(Orders order : orders)
				order.setStatus(OrderStatus.COMPLETED);
			shopService.createOrder(orders,(Long)session.getAttribute("memberId"),cartOrder);
			return "shop/payment-success";
		}
		else {
			return "shop/payment-fail";
		}
	}
	@GetMapping("myOrders")
	public String myOrders(Model model,HttpSession session) {
		model.addAttribute("myOrder",shopService.myOrder((Long)session.getAttribute("memberId")));
		return "shop/myOrder";
	}
	@GetMapping("direct/order/{id}")
	public String directOrder(@PathVariable("id")Long productId,Model model) {
		model.addAttribute("form",new DIrectOrderForm());
		return "shop/directOrder";
	}
	@PostMapping("direct/order/{id}")
	public String directOrder(Model model, @PathVariable("id")Long productId,@ModelAttribute("form")DIrectOrderForm form,HttpSession session) {
		Orders order = new Orders();
		Product product= shopService.getProduct(productId);
		List<Orders> orders = new ArrayList<>();
		order.setAddress(form.getAddress());
		order.setPhone(form.getPhone());
		order.setQuantity(form.getQuantity());
		order.setMemberId((Long)session.getAttribute("memberId"));
		order.setProductId(productId);
		order.setProductName(product.getName());
		order.setSellerId(product.getMemberId());
		order.setStatus(OrderStatus.COMPLETED);
		order.setTotalPrice(product.getPrice()*form.getQuantity());
		orders.add(order);
		session.setAttribute("orders", orders);
		session.setAttribute("cartOrder", false);
		model.addAttribute("totalPrice",order.getTotalPrice());
		return "shop/payment";
	}
	@GetMapping("myProduct/order/list")
	public String myProduct_order_list(HttpSession session,Model model) {
		Member member = memberService.info((Long)session.getAttribute("memberId"));
		if(!member.isSeller())
			return "shop/notSeller";

		List<Orders> orders = shopService.myProductOrderList((Long)session.getAttribute("memberId"));
		model.addAttribute("orders",orders);
		return "shop/myProduct-order-list";
	}
	@GetMapping("order/received/{id}")
	public String order_received(@PathVariable("id") Long orderId,HttpSession session) {
		Orders order = shopService.getOrder(orderId);
		Long memberId = (Long)session.getAttribute("memberId");
		if(!memberId.equals(order.getSellerId()))
			return "admin/notAdmin";

		shopService.orderReceived(orderId);
		return "redirect:/shop/myProduct/order/list";
	}
	@GetMapping("order/preparing/{id}")
	public String order_preparing(@PathVariable("id") Long orderId,HttpSession session) {
		Orders order = shopService.getOrder(orderId);
		Long memberId = (Long)session.getAttribute("memberId");
		if(!memberId.equals(order.getSellerId()))
			return "admin/notAdmin";

		shopService.orderPreparing(orderId);
		return "redirect:/shop/myProduct/order/list";
	}
	@GetMapping("order/shipped/{id}")
	public String order_shipped(@PathVariable("id") Long orderId,HttpSession session) {
		Orders order = shopService.getOrder(orderId);
		Long memberId = (Long)session.getAttribute("memberId");
		if(!memberId.equals(order.getSellerId()))
			return "admin/notAdmin";

		shopService.orderShipped(orderId);
		return "redirect:/shop/myProduct/order/list";
	}
	@GetMapping("order/delivered/{id}")
	public String order_delivered(@PathVariable("id") Long orderId,HttpSession session) {
		Orders order = shopService.getOrder(orderId);
		Long memberId = (Long)session.getAttribute("memberId");
		if(!memberId.equals(order.getMemberId()))
			return "admin/notAdmin";

		shopService.orderDelivered(orderId);
		return "redirect:/shop/myOrders";
	}

	@GetMapping("order/cancel/request/{id}")
	public String order_cancel_request(@PathVariable("id") Long orderId,HttpSession session) {
		Orders order = shopService.getOrder(orderId);
		Long memberId = (Long)session.getAttribute("memberId");
		if(!memberId.equals(order.getMemberId()))
			return "admin/notAdmin";

		shopService.orderCancelRequest(orderId);
		return "redirect:/shop/myOrders";
	}
	@GetMapping("order/cancel/request/cancel/{id}")
	public String order_cancel_request_cancel(@PathVariable("id") Long orderId,HttpSession session) {
		Orders order = shopService.getOrder(orderId);
		Long memberId = (Long)session.getAttribute("memberId");
		if(!memberId.equals(order.getMemberId()))
			return "admin/notAdmin";

		shopService.orderCancelRequestCancel(orderId);
		return "redirect:/shop/myOrders";
	}
	@GetMapping("order/cancel/{id}")
	public String order_cancel(@PathVariable("id") Long orderId,HttpSession session) {
		Orders order = shopService.getOrder(orderId);
		Long memberId = (Long)session.getAttribute("memberId");
		if(!memberId.equals(order.getSellerId()))
			return "admin/notAdmin";

		shopService.orderCancel(orderId);
		return "redirect:/shop/myProduct/order/list";
	}
	@GetMapping("order/info/{id}")
	public String order_info(@PathVariable("id") Long orderId,HttpSession session,Model model) {
		Orders order = shopService.getOrder(orderId);
		Member Seller = memberService.info(order.getSellerId());
		Long memberId = (Long)session.getAttribute("memberId");
		if(memberId.equals(order.getMemberId())||memberId.equals(order.getSellerId())) {
			model.addAttribute("order",order);
			model.addAttribute("sellerPhone",Seller.getPhone());
			return "shop/order-info";
		}else {
			return "admin/notAdmin";
		}

	}
}