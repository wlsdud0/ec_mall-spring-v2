package hello.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class SellerReg {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long memberId;
	private String phone;
	private String address;
}
