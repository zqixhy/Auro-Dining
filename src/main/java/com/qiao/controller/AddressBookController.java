package com.qiao.controller;

import com.qiao.common.R;
import com.qiao.entity.AddressBook;
import com.qiao.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * Get all addresses for the current user
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getList(HttpServletRequest request, AddressBook addressBook){
        // Get current user ID from Session
        Long userId = (Long) request.getSession().getAttribute("user");
        addressBook.setUserId(userId);

        List<AddressBook> list = addressBookService.list(addressBook);
        return R.success(list);
    }

    /**
     * Add a new address
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody AddressBook addressBook){
        // Set User ID
        Long userId = (Long) request.getSession().getAttribute("user");
        addressBook.setUserId(userId);

        // Manually populate audit fields (since MyBatis Plus auto-fill is removed)
        addressBook.setCreateTime(LocalDateTime.now());
        addressBook.setUpdateTime(LocalDateTime.now());
        addressBook.setCreateUser(userId);
        addressBook.setUpdateUser(userId);

        addressBookService.save(addressBook);
        return R.success("Save success");
    }

    /**
     * Get address by ID
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook != null){
            return R.success(addressBook);
        }
        return R.error("Address not found");
    }

    /**
     * Update address
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody AddressBook addressBook){
        // Update audit fields
        addressBook.setUpdateTime(LocalDateTime.now());
        addressBook.setUpdateUser((Long) request.getSession().getAttribute("user"));

        addressBookService.update(addressBook);
        return R.success("Update success");
    }

    /**
     * Delete address
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        addressBookService.delete(ids);
        return R.success("Delete success");
    }

    /**
     * Set default address
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(HttpServletRequest request, @RequestBody AddressBook addressBook){
        // Ensure User ID is set
        addressBook.setUserId((Long) request.getSession().getAttribute("user"));

        addressBookService.setDefault(addressBook);
        return R.success(addressBook);
    }

    /**
     * Get the default address
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpServletRequest request){
        Long userId = (Long) request.getSession().getAttribute("user");
        AddressBook addressBook = addressBookService.getDefault(userId);

        if (addressBook != null) {
            return R.success(addressBook);
        }
        return R.error("Default address not found");
    }
}