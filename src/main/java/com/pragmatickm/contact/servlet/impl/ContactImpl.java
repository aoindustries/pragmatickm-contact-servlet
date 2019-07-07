/*
 * pragmatickm-contact-servlet - Contacts nested within SemanticCMS pages and elements in a Servlet environment.
 * Copyright (C) 2013, 2014, 2015, 2016, 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of pragmatickm-contact-servlet.
 *
 * pragmatickm-contact-servlet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pragmatickm-contact-servlet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with pragmatickm-contact-servlet.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pragmatickm.contact.servlet.impl;

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaWriter;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlEncoder.encodeTextInXhtml;
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.Email;
import com.pragmatickm.contact.model.Address;
import com.pragmatickm.contact.model.AddressType;
import com.pragmatickm.contact.model.Contact;
import com.pragmatickm.contact.model.Im;
import com.pragmatickm.contact.model.ImType;
import com.pragmatickm.contact.model.PhoneNumber;
import com.pragmatickm.contact.model.PhoneType;
import com.semanticcms.core.model.ElementContext;
import com.semanticcms.core.model.NodeBodyWriter;
import com.semanticcms.core.servlet.PageIndex;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

final public class ContactImpl {

	private static void writeRow(String header, String value, Writer out) throws IOException {
		if(value != null) {
			out.write("<tr><th>");
			encodeTextInXhtml(header, out);
			out.write("</th><td colspan=\"2\">");
			encodeTextInXhtml(value, out);
			out.write("</td></tr>\n");
		}
	}

	public static void writeContactTable(
		PageIndex pageIndex,
		Writer out,
		ElementContext context,
		Object style,
		Contact contact
	) throws IOException {
		out.write("<table id=\"");
		PageIndex.appendIdInPage(
			pageIndex,
			contact.getPage(),
			contact.getId(),
			new MediaWriter(textInXhtmlAttributeEncoder, out)
		);
		out.write("\" class=\"thinTable contactTable\"");
		if(style != null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(">\n");
		String title = contact.getTitle();
		String first = contact.getFirst();
		String middle = contact.getMiddle();
		String nick = contact.getNick();
		String last = contact.getLast();
		String maiden = contact.getMaiden();
		String suffix = contact.getSuffix();
		String jobTitle = contact.getJobTitle();
		String company = contact.getCompany();
		String department = contact.getDepartment();
		List<Email> emails = contact.getEmails();
		List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
		List<Im> ims = contact.getIms();
		List<String> webPages = contact.getWebPages();
		List<Address> addresses = contact.getAddresses();
		// Hide header for address-only view
		if(
			title != null
			|| first != null
			|| middle != null
			|| nick != null
			|| last != null
			|| maiden != null
			|| suffix != null
			|| jobTitle != null
			|| company != null
			|| department != null
			|| !emails.isEmpty()
			|| !phoneNumbers.isEmpty()
			|| !ims.isEmpty()
			|| !webPages.isEmpty()
			|| addresses.isEmpty() // When no addresses, always display with a full contact header
		) {
			out.write("<thead><tr><th class=\"contactTableHeader\" colspan=\"3\"><div>");
			contact.appendLabel(new MediaWriter(textInXhtmlEncoder, out));
			out.write("</div></th></tr></thead>\n");
		}
		out.write("<tbody>\n");
		writeRow("Title:", title, out);
		writeRow("First:", first, out);
		writeRow("Middle:", middle, out);
		writeRow("Nick:", nick, out);
		writeRow("Last:", last, out);
		writeRow("Maiden:", maiden, out);
		writeRow("Suffix:", suffix, out);
		writeRow("Company:", company, out);
		writeRow("Department:", department, out);
		writeRow("Job Title:", jobTitle, out);
		for(Email email : emails) {
			String emailString = email.toString();
			out.write("<tr><th>Email:</th><td colspan=\"2\"><div class=\"contact_email_address\"><a href=\"mailto:");
			encodeTextInXhtmlAttribute(emailString, out);
			out.write("\">");
			encodeTextInXhtml(emailString, out);
			out.write("</a></div></td></tr>\n");
		}
		for(PhoneNumber phoneNumber : phoneNumbers) {
			PhoneType type = phoneNumber.getType();
			String number = phoneNumber.getNumber();
			String comment = phoneNumber.getComment();
			out.write("<tr><th>");
			encodeTextInXhtml(type.getLabel(), out);
			out.write(":</th><td");
			if(comment==null) out.write(" colspan=\"2\"");
			out.write("><div class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), out);
			out.write("\"><a href=\"tel:");
			encodeTextInXhtmlAttribute(number.replace(' ', '-'), out);
			out.write("\">");
			encodeTextInXhtml(number, out);
			out.write("</a></div></td>");
			if(comment!=null) {
				out.write("<td>");
				encodeTextInXhtml(comment, out);
				out.write("</td>");
			}
			out.write("</tr>\n");
		}
		for(Im im : ims) {
			ImType type = im.getType();
			String handle = im.getHandle();
			String comment = im.getComment();
			out.write("<tr><th>");
			encodeTextInXhtml(type.getLabel(), out);
			out.write(":</th><td");
			if(comment==null) out.write(" colspan=\"2\"");
			out.write("><div class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), out);
			out.write("\">");
			encodeTextInXhtml(handle, out);
			out.write("</div></td>");
			if(comment!=null) {
				out.write("<td>");
				encodeTextInXhtml(comment, out);
				out.write("</td>");
			}
			out.write("</tr>\n");
		}
		for(String webPage : webPages) {
			out.write("<tr><th>Web Page:</th><td colspan=\"2\"><div class=\"contact_web_page\"><a href=\"");
			encodeTextInXhtmlAttribute(webPage, out);
			out.write("\">");
			encodeTextInXhtml(webPage, out);
			out.write("</a></div></td></tr>\n");
		}
		for(Address address : addresses) {
			AddressType type = address.getType();
			out.write("<tr><th class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), out);
			out.write("\" colspan=\"3\"><div>");
			encodeTextInXhtml(type.getLabel(), out);
			out.write("</div></th></tr>\n");
			writeRow("Address 1:", address.getAddress1(), out);
			writeRow("Address 2:", address.getAddress2(), out);
			writeRow("City:", address.getCity(), out);
			writeRow("State/Prov:", address.getStateProv(), out);
			writeRow("ZIP/Postal:", address.getZipPostal(), out);
			writeRow("Country:", address.getCountry(), out);
			writeRow("Comment:", address.getComment(), out);
		}
		BufferResult body = contact.getBody();
		if(body.getLength() > 0) {
			out.write("<tr><td class=\"contactBody\" colspan=\"3\">");
			body.writeTo(new NodeBodyWriter(contact, out, context));
			out.write("</td></tr>\n");
		}
		out.write("</tbody>\n"
				+ "</table>");
	}

	/**
	 * Make no instances.
	 */
	private ContactImpl() {
	}
}