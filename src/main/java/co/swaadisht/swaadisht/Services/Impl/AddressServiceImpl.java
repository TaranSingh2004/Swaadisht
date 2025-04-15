package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.OrderAddressRepository;
import co.swaadisht.swaadisht.Repository.ProductOrderRepository;
import co.swaadisht.swaadisht.Repository.ProductRepository;
import co.swaadisht.swaadisht.Services.AddressService;
import co.swaadisht.swaadisht.entities.OrderAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl  implements AddressService {

    @Autowired
    private OrderAddressRepository addressRepository;

    @Autowired
    private ProductOrderRepository orderRepository;

    @Transactional
    void deleteAddress(Integer userId, Integer addressId) {
        // First verify the address belongs to the user
        OrderAddress address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or doesn't belong to user"));

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void deleteAddress(int userId, Integer addressId) {
        // Verify address belongs to user
        OrderAddress address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or doesn't belong to user"));

        // Check if address is used in any orders
        boolean isUsedInOrders = orderRepository.existsByOrderAddressId(addressId);

        if (isUsedInOrders) {
            throw new RuntimeException("Cannot delete address - it's being used in existing orders");
        }

        addressRepository.delete(address);
    }
}
