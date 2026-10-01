package murach.dao;

import murach.model.User;

public class UserDAO {

    /**
     * Thao tác insert giả lập User vào CSDL
     * @param user Đối tượng User chứa username, password, email
     * @return true nếu insert giả lập thành công
     */
    public static boolean insert(User user) {
        System.out.println("==================================================");
        System.out.println("[UserDAO - MOCK INSERT]");
        if (user != null) {
            System.out.println("-> Insert user thành công vào Database giả lập:");
            System.out.println("   Username : " + user.getUsername());
            System.out.println("   Email    : " + user.getEmail());
            System.out.println("   Password : " + (user.getPassword() != null && !user.getPassword().isEmpty() ? "******" : "(empty)"));
        } else {
            System.out.println("-> Cảnh báo: User cần insert là null!");
        }
        System.out.println("==================================================");
        return true;
    }

    public static boolean insertUser(User user) {
        return insert(user);
    }
}
