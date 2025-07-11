package com.bookStore.bookStore.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bookStore.bookStore.entity.Book;
import com.bookStore.bookStore.entity.MyBookList;
import com.bookStore.bookStore.entity.User;
import com.bookStore.bookStore.service.BookService;
import com.bookStore.bookStore.service.MyBookListService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BookController {
	
	private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("user");
        return userObj != null && ((User) userObj).getRole().equals("ADMIN");
    }
	@Autowired
	private BookService service;
	
	@Autowired
	private MyBookListService myBookService;
	
	@GetMapping("/home")
	public String home() {
	    return "home";
	}
	@GetMapping("/")
	public String rootRedirect() {
	    return "redirect:/home";
	}
	@GetMapping("/book_register")
    public String bookRegister(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/home";
        return "bookRegister";
    }
    @GetMapping("/available_books")
    public ModelAndView getAllBook(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }

        List<Book> list = service.getAllBook();
        ModelAndView mv = new ModelAndView("bookList");
        mv.addObject("book", list);

        User user = (User) session.getAttribute("user");
        mv.addObject("role", user.getRole());  // Pass role to frontend
        return mv;
    }
    
    @PostMapping("/save")
    public String addBook(@ModelAttribute Book b, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/home";
        service.save(b);
        return "redirect:/available_books";
    }

    @GetMapping("/my_books")
    public String getMyBooks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole().equals("ADMIN")) {
            return "redirect:/home"; // redirect if not logged in or is admin
        }

        List<MyBookList> list = myBookService.getAllMyBooks();
        model.addAttribute("book", list);
        return "myBooks";
    }
    @RequestMapping("/mylist/{id}")
    public String getMyList(@PathVariable("id") int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole().equals("ADMIN")) {
            return "redirect:/home";
        }

        Book b = service.getBookById(id);
        MyBookList mb = new MyBookList(b.getId(), b.getName(), b.getAuthor(), b.getPrice());
        myBookService.saveMyBooks(mb);
        return "redirect:/my_books";
    }
    @GetMapping("/editBook/{id}")
    public String editBook(@PathVariable("id") int id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/home";
        Book b = service.getBookById(id);
        model.addAttribute("book", b);
        return "bookEdit";
    }
    @GetMapping("/deleteBook/{id}")
    public String deleteBook(@PathVariable("id") int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/home";
        service.deleteById(id);
        return "redirect:/available_books";
    }
}
