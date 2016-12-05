package com.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import library.CompressFileToZip;
import library.DecompressZipToFiles;
import library.FileDownload;
import szyfrowanie.OpenFile;
import szyfrowanie.SzyfrowanieASE;

@Controller
public class HomeController 
{
	// STRONA GŁÓWNA
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) 
	{
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
		
	// ROZPOZANAWANIE KTORA STRONA MA ZOSTAC OTWORZNA
	@RequestMapping(value = "/answer", method = RequestMethod.GET)
	public String answer(Model model, @RequestParam("person") String person, @RequestParam(required = false, value = "website") String website)
	{
		if(person.equals("damian"))
		{
			if(website.startsWith("www."))
			{
				website = "http://" + website;
			}
			else if(website.startsWith("http://"))
			{
				
			}
			else 
			{
				website = "http://www.wp.pl";
			}
			
			model.addAttribute("nazwa", website);
			
			String strona = TestDamian(website); 
			model.addAttribute("strona", strona);
			return "damian";
			
		}
		else
		{
			 try 
			    {
					String szyfrowanie = TestMaciej();
					model.addAttribute("szyfrowanie", szyfrowanie);
				} 
			    catch (Exception e) 
			    {
					e.printStackTrace();
				}
				return "maciej";
		}
	}
	
	// POBERANIE PLIKU DAMIAN
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	private void download(HttpServletResponse response, @RequestParam("format") String format)
	{
		String src;
		if(format.equals("txt"))
		{
			src = "Plik1.txt";
			response.setContentType("txt/plain");

		}
		else
		{
			src = "Zip1.zip";
		}
		
		try 
		{
			InputStream is = new FileInputStream(src);
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	private String TestMaciej() throws Exception, Exception 
	{
		SzyfrowanieASE szyfrowanie = new SzyfrowanieASE();
		System.out.println("Podaj klucz: ");
		Scanner scan = new Scanner(System.in);
		String pass = scan.nextLine();
		OpenFile of = new OpenFile();
		String fileContent = of.openFile();
		try 
		{
			szyfrowanie.szyfrowanie(fileContent,pass );
		} 
		catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) 
		{
			e.printStackTrace();
		}
		return fileContent;
	}

	//METODA DAMIAN
	public String TestDamian(String web)
	{
		URL url;
		String str = "";
		try 
		{
			//url = new URL("http://www.wp.pl");
			url = new URL(web);
			FileDownload fileDownload = new FileDownload(url, "Plik1.txt");
			fileDownload.Download();
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
		
		CompressFileToZip compressFileToZip = new CompressFileToZip("Plik1.txt", "Zip1");		
		DecompressZipToFiles decompressFileToZip = new DecompressZipToFiles("Zip1.zip", "Folder");
		
		try 	
		{
			compressFileToZip.Compress();
			decompressFileToZip.Decompress();
			
			BufferedReader br = new BufferedReader(new FileReader("Plik1.txt"));
			try 
			{
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) 
			    {
			        sb.append(line + "\n");
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    str = sb.toString();
			} 
			finally 
			{
			    br.close();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return str;
	}
}
