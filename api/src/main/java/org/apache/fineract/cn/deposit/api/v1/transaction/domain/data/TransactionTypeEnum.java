/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.deposit.api.v1.transaction.domain.data;

/**
 * @code org.apache.fineract.cn.accounting.service.internal.repository.AccountEntryEntity.type
 */
public enum TransactionTypeEnum {

    ACCOUNT_OPENING("ACCO"),
    ACCOUNT_CLOSING("ACCC"),
    ACCOUNT_TRANSFER("ACCT"),

    ACH_CREDIT("ACDT"),
    ACH_DEBIT("ADBT"),
    ACH_ADJUSTMENTS("ADJT"),
    ACH_PREAUTHORISED("APAC"),
    ACH_RETURN("ARET"),
    ACH_REVERSAL("AREV"),
    ACH_SETTLEMENT("ASET"),
    ACH_TRANSACTION("ATXN"),

    ARP_DEBIT("ARPD"),
    CURRENCY_DEPOSIT("FCDP"),
    CURRENCY_WITHDRAWAL("FCWD"),

    BRANCH_TRANSFER("BACT"),
    BRANCH_DEPOSIT("BCDP"),
    BRANCH_WITHDRAWAL("BCWD"),

    DEPOSIT("CDPT"),
    WITHDRAWAL("CWDL"),
    CASH_LETTER("CASH"),

    CHEQUE("CCHQ"),
    BRANCH_CHEQUE("BCHQ"),
    CERTIFIED_CHEQUE("CCCH"),
    CROSSED_CHEQUE("CRCQ"),
    CHEQUE_REVERSAL("CQRV"),
    CHEQUE_OPEN("OPCQ"),
    CHEQUE_ORDER("ORCQ"),

    CHARGES_PAYMENT("CHRG"),
    FEES_PAYMENT("FEES"),
    TAXES_PAYMENT("TAXE"),
    PRINCIPAL_PAYMENT("PPAY"),
    INTEREST_PAYMENT("INTR"),

    DIRECTDEBIT_PAYMENT("PMDD"),
    DIRECTDEBIT_PREAUTHORISED("PADD"),
    BRANCH_DIRECTDEBIT("BBDD"),

    SMARTCARD_PAYMENT("SMRT"),
    POINTOFSALE_CREDITCARD("POSC"),
    POINTOFSALE_DEBITCARD("POSD"),
    POINTOFSALE_PAYMENT("POSP"),

    DOMESTIC_CREDIT("DMCT"),
    INTRACOMPANY_TRANSFER("ICCT"),

    MIXED_DEPOSIT("MIXD"),
    MISCELLANEOUS_DEPOSIT("MSCD"),
    OTHER("OTHR"),

    CONTROLLED_DISBURSEMENT("CDIS"),
    CONTROLLED_DISBURSEMENT2("DSBR"),
    CREDIT_ADJUSTMENT("CAJT"),
    DEBIT_ADJUSTMENT("DAJT"),
    EXCHANGERATE_ADJUSTMENT("ERTA"),
    YID_ADJUSTMENT("YTDA"),
    REIMBURSEMENT("RIMB"),
    DRAWDOWN("DDWN"),

    ERROR_NOTAVAILABLE("NTAV"),
    ERROR_POSTING("PSTE"),
    ERROR_CANCELLATION("RCDD"),
    ERROR_CANCELLATION2("RPCR"),
    ERROR_ZEROBALANCE("ZABA"),
    ;

    private final String code;

    TransactionTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
