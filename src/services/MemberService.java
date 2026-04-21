package services;

import dao.MemberDAO;
import models.Member;

public class MemberService {

    private MemberDAO memberDAO = new MemberDAO();

    public void addMember(int id, String name, String email, String type) {
        Member member = new Member(id, name, email, type);
        memberDAO.addMember(member);
    }

    public void viewMembers() {
        memberDAO.getAllMembers();
    }

    public void updateMember(int id, String name, String email) {
        memberDAO.updateMember(id, name, email);
    }

    public void deleteMember(int id) {
        memberDAO.deleteMember(id);
    }

    public void searchMember(String keyword) {
        memberDAO.searchMember(keyword);
    }
}