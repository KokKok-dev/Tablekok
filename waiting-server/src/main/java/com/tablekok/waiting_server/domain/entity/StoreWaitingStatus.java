package com.tablekok.waiting_server.domain.entity;

import java.util.UUID;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "p_store_waiting_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreWaitingStatus extends BaseEntity {
	@Id
	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Column(name = "latest_assigned_number", nullable = false)
	private Integer latestAssignedNumber = 0; // 마지막으로 발급된 웨이팅 번호

	@Column(name = "current_calling_number", nullable = false)
	private Integer currentCallingNumber = 0; // 현재 매장에서 호출 중인 가장 최근 대기 번호

}
