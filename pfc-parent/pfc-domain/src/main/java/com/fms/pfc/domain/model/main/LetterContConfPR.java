package com.fms.pfc.domain.model.main;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LETTER_CONF_PR")
public class LetterContConfPR  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2624374022260517283L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lt_conf_pr_id", nullable = false)
	private Integer ltConfPrId;

	@Column(name = "lt_conf_id", nullable = false)
	private Integer ltConfId;
	
	@Column(name = "pr_id", nullable = false)
	private Integer prId;
	

	public LetterContConfPR() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LetterContConfPR(Integer ltConfPrId, Integer ltConfId, Integer prId) {
		super();
		this.ltConfPrId = ltConfPrId;
		this.ltConfId = ltConfId;
		this.prId = prId;
	}

	public Integer getLtConfPrId() {
		return ltConfPrId;
	}

	public void setLtConfPrId(Integer ltConfPrId) {
		this.ltConfPrId = ltConfPrId;
	}

	public Integer getLtConfId() {
		return ltConfId;
	}

	public void setLtConfId(Integer ltConfId) {
		this.ltConfId = ltConfId;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}	

}
