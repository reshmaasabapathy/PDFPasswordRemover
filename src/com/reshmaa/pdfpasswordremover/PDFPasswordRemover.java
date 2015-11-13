package com.reshmaa.pdfpasswordremover;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Servlet implementation class PDFPasswordRemover
 */
@WebServlet("/PDFPasswordRemover")
@MultipartConfig(maxFileSize = 10*1024*1024,maxRequestSize = 20*1024*1024,fileSizeThreshold = 5*1024*1024)
public class PDFPasswordRemover extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PDFPasswordRemover() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Part file= request.getPart("file");
		String password = request.getParameter("password");
		PDDocument doc = PDDocument.load(file.getInputStream());
		if(doc.isEncrypted())
		{
			try {
				doc.decrypt(password);
				doc.setAllSecurityToBeRemoved(true);
				response.setHeader("Content-Disposition","attachment;filename='file.pdf'");
				response.setContentType("application/octet-stream");
				ServletOutputStream out=response.getOutputStream();
				doc.save(out);
				out.close();
			} 
			catch (CryptographyException e) {
				PrintWriter out=response.getWriter();
				out.write("Password Incorrect");
				out.close();
			} catch (COSVisitorException e) {
				PrintWriter out=response.getWriter();
				out.write("Exception Occurred: " + e.getMessage());
				out.close();
			}
		} else {
			PrintWriter out=response.getWriter();
			out.write("The Uploaded PDF Document is not Password Protected.");
			out.close();
		}
	}

}
