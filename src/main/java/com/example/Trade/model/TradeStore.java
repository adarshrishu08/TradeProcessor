package com.example.Trade.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "TRADE_STORE")
public class TradeStore {

    @Id
    @Column(name = "ID")
    private BigDecimal id;

    @Column(name = "TRADE_ID")
    private String tradeId;

    @Column(name = "VERSION")
    private BigDecimal version;

    @Column(name = "COUNTER_PARTY_ID")
    private String counterPartyId;

    @Column(name = "BOOK_ID")
    private String bookId;

    @Column(name = "MATUARITY_DATE")
    private Date matuarityDate;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "EXPIRED")
    private String expired;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public void setCounterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Date getMatuarityDate() {
        return matuarityDate;
    }

    public void setMatuarityDate(Date matuarityDate) {
        this.matuarityDate = matuarityDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }
}
