package com.boeingmerryho.business.membershipservice.domain.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

import io.github.boeingmerryho.commonlibrary.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_membership")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Membership extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer season;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MembershipType name;

	@Column(nullable = false)
	private Double discount;

	// @OneToMany(mappedBy = "membership")
	// private List<MembershipUser> users = new ArrayList<>();
}
