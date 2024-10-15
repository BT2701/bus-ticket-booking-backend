package com.example.demo.Controller;

import com.example.demo.DTO.ResponseDTO;
import com.example.demo.DTO.RoleAssignmentDTO;
import com.example.demo.DTO.RoleDTO;
import com.example.demo.Model.Role;
import com.example.demo.Service.RoleSV;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleCTL {
    private final RoleSV roleService;

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .data(roles)
                        .message("Lấy toàn bộ roles thành công !")
                        .status(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO) {
        try {
            Role role = roleService.createRole(roleDTO.getRoleName());
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .data(role)
                            .message("Tạo role thành công !")
                            .status(HttpStatus.CREATED.value())
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<?> getRoleByName(@PathVariable String roleName) {
        try {
            Role role = roleService.getRoleByName(roleName);
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .data(role)
                            .message("Lấy role thành công !")
                            .status(HttpStatus.ACCEPTED.value())
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestBody RoleDTO role
    ) {
        try {
            Role updatedRole = roleService.updateRole(id, role.getRoleName());
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .data(updatedRole)
                            .message("Cập nhật role thành công !")
                            .status(HttpStatus.ACCEPTED.value())
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .data(null)
                            .message("Xóa role thành công !")
                            .status(HttpStatus.NO_CONTENT.value())
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignRoleToCustomer(
            @RequestBody RoleAssignmentDTO roleAssignmentDTO
            ) {
        try {
            roleService.assignRoleToCustomer(roleAssignmentDTO.getCustomerId(), roleAssignmentDTO.getRoleId());
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .data(null)
                            .message("Gán role " + roleAssignmentDTO.getRoleId() + " cho người dùng có id : " + roleAssignmentDTO.getCustomerId() + " thành công !")
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return CustomersCTL.handleError(e);
        }
    }
}
