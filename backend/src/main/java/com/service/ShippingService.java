//package com.service;
//
//public class ShippingService {
//
//    public static ShippingResult calculateShippingFee(ShippingData data, List<Product> productDatabase) {
//        if (data.items == null || data.items.isEmpty()) {
//            throw new IllegalArgumentException("Danh sách sản phẩm không hợp lệ");
//        }
//        if (data.shippingAddress == null || data.shippingAddress.isEmpty()) {
//            throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống");
//        }
//        if (data.deliveryMethod == null || data.deliveryMethod.isEmpty()) {
//            throw new IllegalArgumentException("Phương thức giao hàng không được để trống");
//        }
//
//        String region = getRegionFromAddress(data.shippingAddress);
//
//        boolean isEligibleForExpress =
//                "EXPRESS".equalsIgnoreCase(data.deliveryMethod) &&
//                        (isHanoiInnerCity(data.shippingAddress) || isHCMCInnerCity(data.shippingAddress));
//
//        double totalWeight = 0;
//        double rushOrderWeight = 0;
//
//        for (Item item : data.items) {
//            Product product = findProduct(productDatabase, item.productID, item.variantID);
//            if (product == null) {
//                throw new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + item.productID);
//            }
//            double itemWeight = product.weight * item.quantity;
//            totalWeight += itemWeight;
//            if ("EXPRESS".equalsIgnoreCase(data.deliveryMethod) && product.supportRushOrder) {
//                rushOrderWeight += itemWeight;
//            }
//        }
//
//        double shippingFee = calculateFeeByWeightAndRegion(totalWeight, region, data.deliveryMethod, isEligibleForExpress, rushOrderWeight);
//
//        boolean freeShippingEligible = checkFreeShippingEligibility(data.items, productDatabase, totalWeight);
//
//        ShippingResult result = new ShippingResult();
//        result.fee = freeShippingEligible ? 0 : shippingFee;
//        result.totalWeight = totalWeight;
//        result.rushOrderWeight = rushOrderWeight;
//        result.region = region;
//        result.deliveryMethod = data.deliveryMethod;
//        result.isEligibleForExpress = isEligibleForExpress;
//        result.freeShippingEligible = freeShippingEligible;
//
//        return result;
//    }
//}
