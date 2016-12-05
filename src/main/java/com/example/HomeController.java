package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) 
	{
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
		
	@RequestMapping(value = "/answer", method = RequestMethod.GET)
	public String answer(Model model, @RequestParam("person") String person)
	{
		if(person.equals("damian"))
		{
			String strona = TestDamian(); 
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

	public String TestDamian()
	{
		URL url;
		String str = "";
		try 
		{
			url = new URL("http://www.wp.pl");
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
