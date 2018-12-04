package app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ManagePersonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Phonebook phonebook;

	public ManagePersonServlet() {
		super();

		try {
			this.phonebook = Phonebook.getInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private String validatePersonFMLName(Person person) {
		String error_message = "";
		if (!person.validateFMLNamePart(person.getName(), false)) {
			error_message += "Имя должно быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}
		if (!person.validateFMLNamePart(person.getSurname(), false)) {
			error_message += "Фамилия должна быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}
		if (!person.validateFMLNamePart(person.getMiddlename(), true)) {
			error_message += "Отчество должно быть строкой от 0 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}
		return error_message;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		request.setAttribute("phonebook", this.phonebook);

		HashMap<String, String> jsp_parameters = new HashMap<String, String>();

		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/EditPerson.jsp");
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");
		RequestDispatcher dispatcher_for_addPerson = request.getRequestDispatcher("/AddPerson.jsp");
		RequestDispatcher dispatcher_for_addPhone = request.getRequestDispatcher("/AddPhone.jsp");
		RequestDispatcher dispatcher_for_editPhone = request.getRequestDispatcher("/EditPhone.jsp");

		String action = request.getParameter("action");
		String id = request.getParameter("id");
		String idPhone = request.getParameter("idPhone");

		if ((action == null) && (id == null)) {
			request.setAttribute("jsp_parameters", jsp_parameters);
			dispatcher_for_list.forward(request, response);
		}

		else {
			switch (action) {

			case "add":
				Person empty_person = new Person();

				jsp_parameters.put("current_action", "add");
				jsp_parameters.put("next_action", "add_go");
				jsp_parameters.put("next_action_label", "Добавить");

				request.setAttribute("person", empty_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_addPerson.forward(request, response);
				break;

			case "edit":
				Person editable_person = this.phonebook.getPerson(id);

				jsp_parameters.put("current_action", "edit");
				jsp_parameters.put("next_action", "edit_go");
				jsp_parameters.put("next_action_label", "Сохранить");

				request.setAttribute("person", editable_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_manager.forward(request, response);
				break;

			case "delete":

				if (phonebook.deletePerson(id)) {
					jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
				} else {
					jsp_parameters.put("current_action_result", "DELETION_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
				}

				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_list.forward(request, response);
				break;

			case "addPhone":

				Person personAddPhone = this.phonebook.getPerson(id);

				jsp_parameters.put("current_action", "addPhone");
				jsp_parameters.put("next_action", "addPhone_go");
				jsp_parameters.put("next_action_label", "Добавить номер");

				request.setAttribute("person", personAddPhone);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_addPhone.forward(request, response);
				break;

			case "editPhone":

				Person personEditPhone = this.phonebook.getPerson(id);

				jsp_parameters.put("current_action", "editPhone");
				jsp_parameters.put("next_action", "editPhone_go");
				jsp_parameters.put("next_action_label", "Сохранить номер");

				request.setAttribute("person", personEditPhone);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_editPhone.forward(request, response);
				break;

			case "deletePhone":

				Person personDeletePhone = this.phonebook.getPerson(id);

				if (phonebook.deleteNumber(id, idPhone)) {
					jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
				} else {
					jsp_parameters.put("current_action_result", "DELETION_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
				}

				jsp_parameters.put("current_action", "edit");
				jsp_parameters.put("next_action", "edit_go");
				jsp_parameters.put("next_action_label", "Сохранить");

				request.setAttribute("person", personDeletePhone);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_manager.forward(request, response);
				break;

			}
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		request.setAttribute("phonebook", this.phonebook);

		HashMap<String, String> jsp_parameters = new HashMap<String, String>();

		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/EditPerson.jsp");
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");
		RequestDispatcher dispatcher_for_addPerson = request.getRequestDispatcher("/AddPerson.jsp");
		RequestDispatcher dispatcher_for_addPhone = request.getRequestDispatcher("/AddPhone.jsp");
		RequestDispatcher dispatcher_for_editPhone = request.getRequestDispatcher("/EditPhone.jsp");

		String add_go = request.getParameter("add_go");
		String edit_go = request.getParameter("edit_go");
		String id = request.getParameter("id");
		String addPhone_go = request.getParameter("addPhone");
		String editPhone_go = request.getParameter("editPhone");

		if (add_go != null) {

			Person new_person = new Person(request.getParameter("name"), request.getParameter("surname"),
					request.getParameter("middlename"));

			String error_message = this.validatePersonFMLName(new_person);

			if (error_message.equals("")) {

				if (this.phonebook.addPerson(new_person)) {
					jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
				} else {
					jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка добавления");
				}

				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_list.forward(request, response);
			} else {
				jsp_parameters.put("current_action", "add");
				jsp_parameters.put("next_action", "add_go");
				jsp_parameters.put("next_action_label", "Добавить");
				jsp_parameters.put("error_message", error_message);

				request.setAttribute("person", new_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_addPerson.forward(request, response);
			}
		}

		if (edit_go != null) {
			Person updatable_person = this.phonebook.getPerson(request.getParameter("id"));
			updatable_person.setName(request.getParameter("name"));
			updatable_person.setSurname(request.getParameter("surname"));
			updatable_person.setMiddlename(request.getParameter("middlename"));

			String error_message = this.validatePersonFMLName(updatable_person);

			if (error_message.equals("")) {

				if (this.phonebook.updatePerson(id, updatable_person)) {
					jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
				} else {
					jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка обновления");
				}

				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_list.forward(request, response);
			} else {

				jsp_parameters.put("current_action", "edit");
				jsp_parameters.put("next_action", "edit_go");
				jsp_parameters.put("next_action_label", "Сохранить");
				jsp_parameters.put("error_message", error_message);

				request.setAttribute("person", updatable_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_manager.forward(request, response);

			}
		}
		if (addPhone_go != null) {

			String number = request.getParameter("number");

			String error_message = this.validateNumber(number);

			if (error_message.equals("")) {

				Person person = this.phonebook.getPerson(id);

				jsp_parameters.put("current_action", "edit");
				jsp_parameters.put("next_action", "edit_go");
				jsp_parameters.put("next_action_label", "Сохранить");

				request.setAttribute("person", person);

				if (this.phonebook.addNumber(person, number)) {
					jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
				}

				else {
					jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка обновления");
				}

				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_manager.forward(request, response);
			} else {

				Person person = this.phonebook.getPerson(id);

				jsp_parameters.put("current_action", "addPhone");
				jsp_parameters.put("next_action", "addPhone_go");
				jsp_parameters.put("next_action_label", "Добавить номер");
				jsp_parameters.put("error_message", error_message);

				request.setAttribute("person", person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_addPhone.forward(request, response);
			}
		}

		if (editPhone_go != null) {

			String number = request.getParameter("number");

			String idPhone = request.getParameter("idPhone");

			String error_message = this.validateNumber(number);

			if (error_message.equals("")) {

				Person person = this.phonebook.getPerson(id);

				jsp_parameters.put("current_action", "edit");
				jsp_parameters.put("next_action", "edit_go");
				jsp_parameters.put("next_action_label", "Сохранить");

				request.setAttribute("person", person);

				if (this.phonebook.updateNumber(id, idPhone, number)) {
					jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
				} else {
					jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка обновления");
				}

				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_manager.forward(request, response);
			}

			else {

				Person person = this.phonebook.getPerson(id);

				jsp_parameters.put("idPhone", idPhone);
				jsp_parameters.put("current_action", "editPhone");
				jsp_parameters.put("next_action", "editPhone_go");
				jsp_parameters.put("next_action_label", "Сохранить номер");
				jsp_parameters.put("error_message", error_message);

				request.setAttribute("person", person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_editPhone.forward(request, response);
			}
		}
	}

	private String validateNumber(String number) {
		return null;
	}
}
