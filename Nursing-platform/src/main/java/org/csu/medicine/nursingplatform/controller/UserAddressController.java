package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.UserAddress;
import org.csu.medicine.nursingplatform.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class UserAddressController {

    @Autowired
    private UserAddressService addressService;

    /**
     * 添加地址
     */
    @PostMapping
    public ResponseEntity<UserAddress> addAddress(@RequestBody UserAddress address) {
        return ResponseEntity.ok(addressService.addAddress(address));
    }

    /**
     * 更新地址
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserAddress> updateAddress(
            @PathVariable Long id,
            @RequestBody UserAddress address) {
        address.setId(id); // 确保ID一致
        return ResponseEntity.ok(addressService.updateAddress(address));
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取用户所有地址
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAddress>> getUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    /**
     * 获取用户默认地址
     */
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<UserAddress> getDefaultAddress(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getDefaultAddress(userId));
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/{addressId}/set-default/user/{userId}")
    public ResponseEntity<Void> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok().build();
    }
    /**
     * 获取指定地址详情
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<UserAddress> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }



}