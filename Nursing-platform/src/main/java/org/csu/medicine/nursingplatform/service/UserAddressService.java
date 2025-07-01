package org.csu.medicine.nursingplatform.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.csu.medicine.nursingplatform.entity.UserAddress;
import org.csu.medicine.nursingplatform.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressService {

    @Autowired
    private UserAddressMapper addressMapper;

    /**
     * 添加新地址
     */
    @Transactional
    public UserAddress addAddress(UserAddress address) {
        // 如果设置为默认地址，先清除该用户的其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefaultAddress(address.getUserId());
        }

        // 插入新地址
        addressMapper.insert(address);
        return address;
    }

    /**
     * 更新地址
     */
    @Transactional
    public UserAddress updateAddress(UserAddress address) {
        // 获取当前数据库中的地址
        UserAddress existing = addressMapper.selectById(address.getId());
        if (existing == null) {
            throw new RuntimeException("地址不存在");
        }

        // 验证地址属于该用户
        if (!existing.getUserId().equals(address.getUserId())) {
            throw new RuntimeException("不能修改用户ID");
        }

        // 如果设置为默认地址，先清除该用户的其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefaultAddress(address.getUserId());
        }

        // 更新地址
        addressMapper.updateById(address);
        return address;
    }

    /**
     * 删除地址
     */
    @Transactional
    public void deleteAddress(Long id) {
        UserAddress address = addressMapper.selectById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }

        addressMapper.deleteById(id);

        // 如果删除的是默认地址，则设置一个新的默认地址
        if (address.getIsDefault() == 1) {
            setNewDefaultAddress(address.getUserId());
        }
    }

    /**
     * 设置新的默认地址（当删除默认地址时调用）
     */
    private void setNewDefaultAddress(Long userId) {
        // 获取该用户最近添加的地址
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT 1");

        UserAddress recentAddress = addressMapper.selectOne(queryWrapper);

        // 如果还有地址，则设置第一个为默认
        if (recentAddress != null) {
            recentAddress.setIsDefault(1);
            addressMapper.updateById(recentAddress);
        }
    }

    /**
     * 获取用户所有地址
     */
    public List<UserAddress> getUserAddresses(Long userId) {
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("is_default")
                .orderByDesc("create_time");
        return addressMapper.selectList(queryWrapper);
    }

    /**
     * 获取用户默认地址
     */
    public UserAddress getDefaultAddress(Long userId) {
        return addressMapper.findDefaultAddressByUserId(userId);
    }

    /**
     * 设置默认地址
     */
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 验证地址属于该用户
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或不属于该用户");
        }

        // 清除该用户的其他默认地址
        addressMapper.clearDefaultAddress(userId);

        // 设置新默认地址
        address.setIsDefault(1);
        addressMapper.updateById(address);
    }
    public UserAddress getAddressById(Long addressId) {
        return addressMapper.selectById(addressId);
    }

}
