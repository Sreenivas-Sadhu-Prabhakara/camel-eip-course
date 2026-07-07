// SPDX-License-Identifier: Apache-2.0
package com.example.eip.enricher;

/**
 * The extra data the Content Enricher pulls in: the full customer record behind an order's
 * {@code customerId}. Returned by {@link CustomerRepository} and merged onto the {@link Order}
 * by {@link MergeStrategy}.
 */
public class Customer {

    private String id;
    private String name;
    private String email;
    private String tier;

    public Customer() {
    }

    public Customer(String id, String name, String email, String tier) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tier = tier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "', email='" + email + "', tier='" + tier + "'}";
    }
}
