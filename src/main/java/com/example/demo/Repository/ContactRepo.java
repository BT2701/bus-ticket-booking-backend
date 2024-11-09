package com.example.demo.Repository;
<<<<<<< HEAD

public class ContactRepo {
=======
import com.example.demo.Model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.security.Timestamp;
import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Integer> {
    // Truy vấn tùy chỉnh để lấy các contact có status = 0 và sắp xếp theo create_at
    @Query("SELECT c FROM contact_us c WHERE c.status = :status ORDER BY c.create_at ASC")
    List<Contact> findContactsByStatus(@Param("status") int status);

    @Modifying
    @Query("UPDATE contact_us c SET c.status = 1, c.update_at = CURRENT_TIMESTAMP WHERE c.id = :id")
    int updateContactStatus(@Param("id") Integer id);

    // Phương thức để lấy trạng thái của yêu cầu theo ID
    @Query("SELECT c.status FROM contact_us c WHERE c.id = :id")
    int getContactStatusById(@Param("id") int id);

>>>>>>> 7ff0c7e0d03a5a208946ea65d332d59e1aa20939
}
