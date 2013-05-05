package com.megadevs.savey.machineserver;

public enum Product {
    ESPRESSO(0.40),
    TEA(0.35),
    CAFFELATTE(0.40),
    ORZO(0.30),
    CAPPUCCINO(0.50),
    CHOCOLATE(0.30);

    double cost;
    Product(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public static Product fromOrdinal(int ordinal) {
        for (Product p : values()) {
            if (p.ordinal() + 1 == ordinal) {
                return p;
            }
        }
        return null;
    }

}
