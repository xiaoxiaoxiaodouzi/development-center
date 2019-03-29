package com.chinacreator.c2.project.testcase;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 测试步骤
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.testcase.CaseStep", table = "c2_community_case_step", ds = "dev-center")
public class CaseStep implements Serializable {
	private static final long serialVersionUID = 2062978623700992L;
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *用例id
	 */
	@Column(id = "case_id", datatype = "int")
	private java.lang.Integer caseId;

	/**
	 *测试步骤
	 */
	@Column(id = "step", datatype = "string2000")
	private java.lang.String step;

	/**
	 *预期结果
	 */
	@Column(id = "expect_result", datatype = "string2000")
	private java.lang.String expectResult;

	/**
	 *排序号
	 */
	@Column(id = "no_", datatype = "int")
	private java.lang.Integer no;

	/**
	 * 设置
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置用例id
	 */
	public void setCaseId(java.lang.Integer caseId) {
		this.caseId = caseId;
	}

	/**
	 * 获取用例id
	 */
	public java.lang.Integer getCaseId() {
		return caseId;
	}

	/**
	 * 设置测试步骤
	 */
	public void setStep(java.lang.String step) {
		this.step = step;
	}

	/**
	 * 获取测试步骤
	 */
	public java.lang.String getStep() {
		return step;
	}

	/**
	 * 设置预期结果
	 */
	public void setExpectResult(java.lang.String expectResult) {
		this.expectResult = expectResult;
	}

	/**
	 * 获取预期结果
	 */
	public java.lang.String getExpectResult() {
		return expectResult;
	}

	/**
	 * 设置排序号
	 */
	public void setNo(java.lang.Integer no) {
		this.no = no;
	}

	/**
	 * 获取排序号
	 */
	public java.lang.Integer getNo() {
		return no;
	}
}
