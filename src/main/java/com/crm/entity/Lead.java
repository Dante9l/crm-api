package com.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@TableName("t_lead")
@ApiModel(value = "Lead对象", description = "")
public class Lead {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("线索名称")
    @TableField("name")
    @NotBlank(message = "请输入线索名称")
    private String name;

    @ApiModelProperty("手机号")
    @TableField("phone")
    @NotBlank(message = "请输入手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("客户级别")
    @TableField("level")
    @NotNull(message = "请选择客户级别")
    private Integer level;

    @ApiModelProperty("客户来源")
    @TableField("source")
    @NotNull(message = "请选择客户来源")
    private Integer source;

    @ApiModelProperty("客户地址")
    @TableField("address")
    private String address;

    @ApiModelProperty("跟进状态")
    @TableField("follow_status")
    private Integer followStatus;

    @ApiModelProperty("下次跟进时间")
    @TableField("next_follow_status")
    private LocalDateTime nextFollowStatus;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("负责人id")
    @TableField("owner_id")
    private Integer ownerId;

    @ApiModelProperty("线索状态")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("逻辑删除")
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleteFlag;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public Integer getId() {
        return this.id;
    }

    public @NotBlank(message = "请输入线索名称") String getName() {
        return this.name;
    }

    public @NotBlank(message = "请输入手机号") String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public @NotNull(message = "请选择客户级别") Integer getLevel() {
        return this.level;
    }

    public @NotNull(message = "请选择客户来源") Integer getSource() {
        return this.source;
    }

    public String getAddress() {
        return this.address;
    }

    public Integer getFollowStatus() {
        return this.followStatus;
    }

    public LocalDateTime getNextFollowStatus() {
        return this.nextFollowStatus;
    }

    public String getRemark() {
        return this.remark;
    }

    public Integer getOwnerId() {
        return this.ownerId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Integer getDeleteFlag() {
        return this.deleteFlag;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(@NotBlank(message = "请输入线索名称") String name) {
        this.name = name;
    }

    public void setPhone(@NotBlank(message = "请输入手机号") String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLevel(@NotNull(message = "请选择客户级别") Integer level) {
        this.level = level;
    }

    public void setSource(@NotNull(message = "请选择客户来源") Integer source) {
        this.source = source;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFollowStatus(Integer followStatus) {
        this.followStatus = followStatus;
    }

    public void setNextFollowStatus(LocalDateTime nextFollowStatus) {
        this.nextFollowStatus = nextFollowStatus;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
