package io.mkalugin.synergy;

import io.mkalugin.synergy.config.AppConfig;
import io.mkalugin.synergy.model.Contact;
import io.mkalugin.synergy.service.ContactService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class SynergyApplication {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		ContactService contactService = context.getBean(ContactService.class);

		List<Contact> contacts = contactService.findAll();
		contacts.forEach(System.out::println);
	}
}