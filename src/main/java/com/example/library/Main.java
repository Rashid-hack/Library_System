package com.example.library;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import com.example.library.repository.InMemoryBookRepository;
import com.example.library.repository.JdbcBookRepository;
import com.example.library.service.BookService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookRepository repo = new JdbcBookRepository("jdbc:h2:./data/library");
        BookService bookService = new BookService(repo);
        Scanner sc = new Scanner(System.in);

        while (true){
            System.out.println("\n=== KITABXANA MENYUSU ===");
            System.out.println("1. Butun kitablari goster.");
            System.out.println("2. Yeni kitab elave et.");
            System.out.println("3. Kitabin qiymetini yenile.");
            System.out.println("4. Kitabi sil.");
            System.out.println("0. Exit...");
            System.out.println("Secimizi daxil edin: ");

            int choice;
            try{
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Xeta: Zehmet olmasa duzgun reqem daxil edin!");
                continue;
            }

            switch (choice){
                case 1:
                    List<Book> books = bookService.getAllBooks();
                    if (books.isEmpty()){
                        System.out.println("Kitabxana heleki bosdur");
                    } else {
                        System.out.println("\n --- Kitablarin siyahisi ---");
                        for (Book book : books){
                            System.out.println(book);
                        }
                    }
                    break;
                case 2:
                    System.out.println("Kitabin adini daxil edin: ");
                    String title = sc.nextLine();

                    System.out.println("Muellifin adinin daxil edin: ");
                    String author = sc.nextLine();

                    System.out.println("Qiymetini daxil edin: ");
                    try {
                        double price = Double.parseDouble(sc.nextLine());
                        Book savedBook = bookService.addBook(title, author, price);
                        System.out.println("Ugurla elave olundu: " + savedBook);
                    } catch (Exception e){
                        System.out.println("Xeta: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Yenilenece kitabin ID-sini daxil edin: ");
                    try {
                        int id = Integer.parseInt(sc.nextLine());
                        System.out.println("Yeni qiymeti daxil edin: ");
                        double newPrice = Double.parseDouble(sc.nextLine());

                        boolean isUpdated = bookService.updatePrice(id, newPrice);
                        if (isUpdated){
                            System.out.println("Kitabin qiymeti ugurla yenilendi.");
                        } else {
                            System.out.println("Bu ID-de kitab tapilmadi!");
                        }
                    } catch (Exception e){
                        System.out.println("Xeta: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Silinecek kitabin ID-sini daxil edin: ");
                    try {
                        int id = Integer.parseInt(sc.nextLine());
                        boolean isDeleted = bookService.deleteById(id);
                        if (isDeleted){
                            System.out.println("Kitab ugurla silindi.");
                        } else {
                            System.out.println("Bu ID-de silinecek kitab tapilmadi!");
                        }
                    } catch (Exception e){
                        System.out.println("Xeta: " + e.getMessage());
                    }
                    break;
                case 0:
                    System.out.println("Programdan cixilir...Sagolun!");
                    sc.close();
                    return;

                default:
                    System.out.println("Yanlis secim, zehmet olmasa 0 ve 4 arasinda reqem secin.");
            }
        }

    }
}
