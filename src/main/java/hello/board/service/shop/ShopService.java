package hello.board.service.shop;

import hello.board.domain.*;
import hello.board.repository.member.MemberRepository;
import hello.board.repository.shop.cart.CartRepository;
import hello.board.repository.shop.cart.JpaCartRepository;
import hello.board.repository.shop.order.JpaOrderRepository;
import hello.board.repository.shop.order.OrderRepository;
import hello.board.repository.shop.product.JpaProductRepository;
import hello.board.repository.shop.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
//쇼핑몰 기능의 서비스 계층
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
	private final JpaProductRepository jpaProductRepository;
	private final ProductRepository productRepository;
	private final JpaOrderRepository jpaOrderRepository;
	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final JpaCartRepository jpaCartRepository;
	private final MemberRepository memberRepository;

	@Value("${image.upload.dir}")
	private String imageUploadDir;

	public List<Product> productList(int page, int maxResult,String category){
		return jpaProductRepository.findByPage(page,maxResult,category);
	}

	public void productReg(Product product) {
		productRepository.save(product);
	}

	public Product getProduct(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	@Transactional
	public void productUpdate(Long productId,Product product) {
		Product findProduct =  productRepository.findById(productId).orElse(null);
		Objects.requireNonNull(findProduct).setName(product.getName());
		findProduct.setPrice(product.getPrice());
		findProduct.setQuantity(product.getQuantity());
		log.info("category = {} ", product.getCategory());
		findProduct.setCategory(product.getCategory());
	}

	public void deleteProduct(Long productId) {
		productRepository.deleteById(productId);
	}

	public long getProductCount(String category) {
		return jpaProductRepository.getCount(category);
	}

	@Transactional
	public void addCart(Member member, Product product, int quantity) {
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setProduct(product);
		cart.setQuantity(quantity);
		cartRepository.save(cart);
	}

	public List<Cart> myCart(Member member) {
		return cartRepository.findAllByMember(member);
	}

	public void deleteCart(Long cartId) {
		cartRepository.deleteById(cartId);
	}

	@Transactional
	public void createOrder(List<Orders> orders,Long memberId, boolean cartOrder) {
		if(cartOrder) {
			List<Cart> carts = jpaCartRepository.findByMember(memberRepository.findById(memberId).orElse(null));
			for(Cart cart : carts)
				cartRepository.deleteById(cart.getId());
		}

		for(Orders order : orders) {
			orderRepository.save(order);
			Product product = productRepository.findById(order.getProductId()).orElse(null);
			Objects.requireNonNull(product).setQuantity(product.getQuantity()-order.getQuantity());
		}
	}

	public List<Orders> myOrder(Long memberId) {
		return jpaOrderRepository.findByMemberId(memberId);
	}

	public List<Orders> myProductOrderList(Long sellerId) {
		return jpaOrderRepository.findBySellerId(sellerId);
	}

	public Orders getOrder(Long orderId) {
		return orderRepository.findById(orderId).orElse(null);
	}

	@Transactional
	public void orderReceived(Long orderId){
		Orders order = orderRepository.findById(orderId).orElse(null);
		Objects.requireNonNull(order).setStatus(OrderStatus.RECEIVED);
	}

	@Transactional
	public void orderPreparing(Long orderId) {
		Orders order = orderRepository.findById(orderId).orElse(null);
		Objects.requireNonNull(order).setStatus(OrderStatus.PREPARING);
	}
	@Transactional
	public void orderShipped(Long orderId) {
		Orders order = orderRepository.findById(orderId).orElse(null);
		Objects.requireNonNull(order).setStatus(OrderStatus.SHIPPED);
	}
	@Transactional
	public void orderDelivered(Long orderId) {
		Orders order = orderRepository.findById(orderId).orElse(null);
		Objects.requireNonNull(order).setStatus(OrderStatus.DELIVERED);
	}
	@Transactional
	public void orderCancelRequest(Long orderId) {
		Orders order = orderRepository.findById(orderId).orElse(null);
		if(Objects.requireNonNull(order).getStatus()==OrderStatus.COMPLETED)
			order.setStatus(OrderStatus.CANCELLED);
		else
			order.setStatus(OrderStatus.CANCEL_REQUEST);
	}
	@Transactional
	public void orderCancelRequestCancel(Long orderId) {
		Orders order = orderRepository.findById(orderId).orElse(null);
		Objects.requireNonNull(order).setStatus(OrderStatus.COMPLETED);
	}

	@Transactional
	public void orderCancel(Long orderId) {
		Orders order = orderRepository.findById(orderId).orElse(null);
		Product product = productRepository.findById(Objects.requireNonNull(order).getProductId()).orElse(null);
		Objects.requireNonNull(product).setQuantity(product.getQuantity()+order.getQuantity());
		order.setStatus(OrderStatus.CANCELLED);
	}
}