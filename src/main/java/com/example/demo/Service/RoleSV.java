package com.example.demo.Service;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Role;
import com.example.demo.Repository.CustomerRepo;
import com.example.demo.Repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleSV {
    private final RoleRepo roleRepository;
    private final CustomerRepo customerRepo;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role createRole(String roleName) {
        Role existingRole = roleRepository.findByName(roleName);
        if (existingRole != null) {
            throw new RuntimeException("Role đã tồn tại: " + roleName);
        }

        Role newRole = Role.builder().name(roleName).build();

        return roleRepository.save(newRole);
    }

    public Role getRoleByName(String roleName) {
        Role role = roleRepository.findByName(roleName);

        if (role == null) {
            throw new RuntimeException("Không tìm thấy role với tên: " + roleName);
        }

        return role;
    }

    public Role updateRole(Long id, String newRoleName) {
        Role existingRole = roleRepository.findByName(newRoleName);
        if (existingRole != null && !existingRole.getId().equals(id)) {
            throw new RuntimeException("Tên role mới đã tồn tại: " + newRoleName);
        }

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role với ID: " + id));

        role.setName(newRoleName);
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role với ID: " + id));

        if (!role.getCustomer().isEmpty()) {
            throw new RuntimeException("Không thể xóa role vì đang có người dùng liên kết.");
        }

        roleRepository.delete(role);
    }

    public void assignRoleToCustomer(Integer customerId, Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isEmpty()) {
            throw new RuntimeException("Role với id : " + roleId + " không tồn tại");
        }

        Customer existingCustomer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + customerId));

        existingCustomer.setRole(role.get());
        customerRepo.save(existingCustomer);
    }
}
