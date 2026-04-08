package com.cursor.retrostore.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 320)
    private String customerEmail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() {
    }

    public Order(BigDecimal totalAmount, String customerEmail) {
        this.totalAmount = totalAmount;
        this.customerEmail = customerEmail;
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public void addLine(OrderLine line) {
        lines.add(line);
        line.setOrder(this);
    }
}
