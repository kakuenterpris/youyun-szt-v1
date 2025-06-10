package com.ustack.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description : 发票内容
 * @Author : LinXin
 * @ClassName : InvoiceContentDTO
 * @Date: 2021-05-17 16:25
 */
@Data
public class InvoiceContentDTO {


    @JsonProperty("inv_title")
    private String invTitle;


    @JsonProperty("inv_code")
    private String invCode;


    @JsonProperty("inv_num")
    private String invNum;


    @JsonProperty("inv_date")
    private String invDate;


    @JsonProperty("inv_check_code")
    private String invCheckCode;


    @JsonProperty("inv_machine_code")
    private String invMachineCode;


    @JsonProperty("inv_buyer_name")
    private String invBuyerName;


    @JsonProperty("inv_buyer_id")
    private String invBuyerId;


    @JsonProperty("inv_buyer_address")
    private String invBuyerAddress;


    @JsonProperty("inv_buyer_account")
    private String invBuyerAccount;


    @JsonProperty("inv_password")
    private String invPassword;


    @JsonProperty("inv_detail_first_name")
    private String invDetailFirstName;


    @JsonProperty("inv_detail_first_val")
    private String invDetailFirstVal;


    @JsonProperty("inv_detail_second_name")
    private String invDetailSecondName;


    @JsonProperty("inv_detail_second_val")
    private String invDetailSecondVal;


    @JsonProperty("inv_detail_third_name")
    private String invDetailThirdName;


    @JsonProperty("inv_detail_third_val")
    private String invDetailThirdVal;


    @JsonProperty("inv_detail_fourth_name")
    private String invDetailFourthName;


    @JsonProperty("inv_detail_fourth_val")
    private String invDetailFourthVal;


    @JsonProperty("inv_detail_fifth_name")
    private String invDetailFifthName;


    @JsonProperty("inv_detail_fifth_val")
    private String invDetailFifthVal;


    @JsonProperty("inv_detail_money")
    private String invDetailMoney;


    @JsonProperty("inv_detail_tax_rate")
    private String invDetailTaxRate;


    @JsonProperty("inv_detail_tax_num")
    private String invDetailTaxNum;


    @JsonProperty("inv_summation_money")
    private String invSummationMoney;


    @JsonProperty("inv_summation_tax_num")
    private String invSummationTaxNum;


    @JsonProperty("inv_tax_up")
    private String invTaxUp;


    @JsonProperty("inv_tax_low")
    private String invTaxLow;


    @JsonProperty("inv_note")
    private String invNote;


    @JsonProperty("inv_seller_name")
    private String invSellerName;


    @JsonProperty("inv_seller_id")
    private String invSellerId;


    @JsonProperty("inv_seller_address")
    private String invSellerAddress;


    @JsonProperty("inv_seller_account")
    private String invSellerAccount;


    @JsonProperty("inv_charge")
    private String invCharge;


    @JsonProperty("inv_checker")
    private String invChecker;


    @JsonProperty("inv_poster")
    private String invPoster;


    @JsonProperty("inv_seller")
    private String invSeller;
}
