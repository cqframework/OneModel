package main;
/*********************************************************************
*
*      Copyright (C) 2001 Andrew Khan
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
***************************************************************************/



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jxl.CellReferenceHelper;
import jxl.CellView;
import jxl.HeaderFooter;
import jxl.Range;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.Orientation;
import jxl.format.PageOrder;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Blank;
import jxl.write.Boolean;
import jxl.write.DateFormat;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


/**
 * Demo class which writes a spreadsheet.  This demo illustrates most of the
 * features of the JExcelAPI, such as text, numbers, fonts, number formats and
 * date formats
 */
public class Write
{
  /**
   * The filename
   */
  private String filename;

  /**
   * The workbook
   */
  private WritableWorkbook workbook;

  /**
   * Constructor
   * 
   * @param fn 
   */
  public Write(String fn)
  {
    filename = fn;
  }

  /**
   * Uses the JExcelAPI to create a spreadsheet
   * 
   * @exception IOException
   * @exception WriteException
   */
  public void write() throws IOException, WriteException
  {
    WorkbookSettings ws = new WorkbookSettings();
    ws.setLocale(new Locale("en", "EN"));
    workbook = Workbook.createWorkbook(new File(filename), ws);


    WritableSheet s1 = workbook.createSheet("Label Formats", 0);

    // Modify the colour palette to bright red for the lime colour
   // workbook.setColourRGB(Colour.LIME, 0xff, 0, 0);


    writeLabelFormatSheet2(s1);

    workbook.write();
    workbook.close();
  }

  public static void main(String[] args) throws IOException, WriteException {
	Write demo = new Write("demo1.xls");
	demo.write();
  }
  
  
  private void writeLabelFormatSheet2(WritableSheet s1) throws WriteException
  {
    s1.setColumnView(0, 60);

    WritableFont arial12ptBold = new WritableFont
    	      (WritableFont.ARIAL, 12, WritableFont.BOLD);
    WritableCellFormat arial12BoldFormat = new WritableCellFormat
    	      (arial12ptBold);
    Label lr = new Label(0,0, "Arial Fonts", arial12BoldFormat);
    s1.addCell(lr);

  }
  
  
  /**
   * Adds cells to the specified sheet which test the various label formatting
   * styles, such as different fonts, different sizes and bold, underline etc.
   * 
   * @param s1 
   */
  
  private void writeLabelFormatSheet(WritableSheet s1) throws WriteException
  {
    s1.setColumnView(0, 60);

    Label lr = new Label(0,0, "Arial Fonts");
    s1.addCell(lr);

    lr = new Label(1,0, "10pt");
    s1.addCell(lr);

    lr = new Label(2, 0, "Normal");
    s1.addCell(lr);

    lr = new Label(3, 0, "12pt");
    s1.addCell(lr);

    WritableFont arial12pt = new WritableFont(WritableFont.ARIAL, 12);
    WritableCellFormat arial12format = new WritableCellFormat(arial12pt);
    arial12format.setWrap(true);
    lr = new Label(4, 0, "Normal", arial12format);
    s1.addCell(lr);

    WritableFont arial10ptBold = new WritableFont
      (WritableFont.ARIAL, 10, WritableFont.BOLD);
    WritableCellFormat arial10BoldFormat = new WritableCellFormat
      (arial10ptBold);
    lr = new Label(2, 2, "BOLD", arial10BoldFormat);
    s1.addCell(lr);

    WritableFont arial12ptBold = new WritableFont
      (WritableFont.ARIAL, 12, WritableFont.BOLD);
    WritableCellFormat arial12BoldFormat = new WritableCellFormat
      (arial12ptBold);
    lr = new Label(4, 2, "BOLD", arial12BoldFormat);
    s1.addCell(lr);

    WritableFont arial10ptItalic = new WritableFont
      (WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
    WritableCellFormat arial10ItalicFormat = new WritableCellFormat
      (arial10ptItalic);
    lr = new Label(2, 4, "Italic", arial10ItalicFormat);
    s1.addCell(lr);

    WritableFont arial12ptItalic = new WritableFont
      (WritableFont.ARIAL, 12, WritableFont.NO_BOLD, true);
    WritableCellFormat arial12ptItalicFormat = new WritableCellFormat
      (arial12ptItalic);
    lr = new Label(4, 4, "Italic", arial12ptItalicFormat);
    s1.addCell(lr);

    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
    WritableCellFormat times10format = new WritableCellFormat(times10pt);
    lr = new Label(0, 7, "Times Fonts", times10format);
    s1.addCell(lr);

    lr = new Label(1, 7, "10pt", times10format);
    s1.addCell(lr);

    lr = new Label(2, 7, "Normal", times10format);
    s1.addCell(lr);

    lr = new Label(3, 7, "12pt", times10format);
    s1.addCell(lr);

    WritableFont times12pt = new WritableFont(WritableFont.TIMES, 12);
    WritableCellFormat times12format = new WritableCellFormat(times12pt);
    lr = new Label(4, 7, "Normal", times12format);
    s1.addCell(lr);

    WritableFont times10ptBold = new WritableFont
      (WritableFont.TIMES, 10, WritableFont.BOLD);
    WritableCellFormat times10BoldFormat = new WritableCellFormat
      (times10ptBold);
    lr = new Label(2, 9, "BOLD", times10BoldFormat);
    s1.addCell(lr);

    WritableFont times12ptBold = new WritableFont
      (WritableFont.TIMES, 12, WritableFont.BOLD);
    WritableCellFormat times12BoldFormat = new WritableCellFormat
      (times12ptBold);
    lr = new Label(4, 9, "BOLD", times12BoldFormat);
    s1.addCell(lr);

    // The underline styles
    s1.setColumnView(6, 22);
    s1.setColumnView(7, 22);
    s1.setColumnView(8, 22);
    s1.setColumnView(9, 22);

    lr = new Label(0, 11, "Underlining");
    s1.addCell(lr);

    WritableFont arial10ptUnderline = new WritableFont
      (WritableFont.ARIAL, 
       WritableFont.DEFAULT_POINT_SIZE,
       WritableFont.NO_BOLD,
       false,
       UnderlineStyle.SINGLE);
    WritableCellFormat arialUnderline = new WritableCellFormat
      (arial10ptUnderline);
    lr = new Label(6,11, "Underline", arialUnderline);
    s1.addCell(lr);

    WritableFont arial10ptDoubleUnderline = new WritableFont
      (WritableFont.ARIAL, 
       WritableFont.DEFAULT_POINT_SIZE,
       WritableFont.NO_BOLD,
       false,
       UnderlineStyle.DOUBLE);
    WritableCellFormat arialDoubleUnderline = new WritableCellFormat
      (arial10ptDoubleUnderline);
    lr = new Label(7,11, "Double Underline", arialDoubleUnderline);
    s1.addCell(lr);

    WritableFont arial10ptSingleAcc = new WritableFont
      (WritableFont.ARIAL, 
       WritableFont.DEFAULT_POINT_SIZE,
       WritableFont.NO_BOLD,
       false,
       UnderlineStyle.SINGLE_ACCOUNTING);
    WritableCellFormat arialSingleAcc = new WritableCellFormat
      (arial10ptSingleAcc);
    lr = new Label(8,11, "Single Accounting Underline", arialSingleAcc);
    s1.addCell(lr);

    WritableFont arial10ptDoubleAcc = new WritableFont
      (WritableFont.ARIAL, 
       WritableFont.DEFAULT_POINT_SIZE,
       WritableFont.NO_BOLD,
       false,
       UnderlineStyle.DOUBLE_ACCOUNTING);
    WritableCellFormat arialDoubleAcc = new WritableCellFormat
      (arial10ptDoubleAcc);
    lr = new Label(9,11, "Double Accounting Underline", arialDoubleAcc);
    s1.addCell(lr);

    WritableFont times14ptBoldUnderline = new WritableFont
      (WritableFont.TIMES,
       14,
       WritableFont.BOLD,
       false,
       UnderlineStyle.SINGLE);
    WritableCellFormat timesBoldUnderline = new WritableCellFormat
      (times14ptBoldUnderline);
    lr = new Label(6,12, "Times 14 Bold Underline", timesBoldUnderline);
    s1.addCell(lr);

    WritableFont arial18ptBoldItalicUnderline = new WritableFont
      (WritableFont.ARIAL,
       18,
       WritableFont.BOLD,
       true,
       UnderlineStyle.SINGLE);
    WritableCellFormat arialBoldItalicUnderline = new WritableCellFormat
      (arial18ptBoldItalicUnderline);
    lr = new Label(6,13, "Arial 18 Bold Italic Underline", 
                   arialBoldItalicUnderline);
    s1.addCell(lr);

    lr = new Label(0, 15, "Script styles");
    s1.addCell(lr);

    WritableFont superscript = new WritableFont
      (WritableFont.ARIAL,
       WritableFont.DEFAULT_POINT_SIZE,
       WritableFont.NO_BOLD,
       false,
       UnderlineStyle.NO_UNDERLINE,
       Colour.BLACK,
       ScriptStyle.SUPERSCRIPT);
    WritableCellFormat superscriptFormat = new WritableCellFormat
      (superscript);
    lr = new Label(1,15, "superscript", superscriptFormat);
    s1.addCell(lr);

    WritableFont subscript = new WritableFont
      (WritableFont.ARIAL,
       WritableFont.DEFAULT_POINT_SIZE,
       WritableFont.NO_BOLD,
       false,
       UnderlineStyle.NO_UNDERLINE,
       Colour.BLACK,
       ScriptStyle.SUBSCRIPT);
    WritableCellFormat subscriptFormat = new WritableCellFormat
      (subscript);
    lr = new Label(2,15, "subscript", subscriptFormat);
    s1.addCell(lr);

    lr = new Label(0, 17, "Colours");
    s1.addCell(lr);

    WritableFont red = new WritableFont(WritableFont.ARIAL, 
                                        WritableFont.DEFAULT_POINT_SIZE,
                                        WritableFont.NO_BOLD,
                                        false,
                                        UnderlineStyle.NO_UNDERLINE,
                                        Colour.RED);
    WritableCellFormat redFormat = new WritableCellFormat(red);
    lr = new Label(2, 17, "Red", redFormat);
    s1.addCell(lr);

    WritableFont blue = new WritableFont(WritableFont.ARIAL, 
                                         WritableFont.DEFAULT_POINT_SIZE,
                                         WritableFont.NO_BOLD,
                                         false,
                                         UnderlineStyle.NO_UNDERLINE,
                                         Colour.BLUE);
    WritableCellFormat blueFormat = new WritableCellFormat(blue);
    lr = new Label(2, 18, "Blue", blueFormat);
    s1.addCell(lr);

    WritableFont lime = new WritableFont(WritableFont.ARIAL);
    lime.setColour(Colour.LIME);
    WritableCellFormat limeFormat = new WritableCellFormat(lime);
    limeFormat.setWrap(true);
    lr = new Label(4, 18, "Modified palette - was lime, now red", limeFormat);
    s1.addCell(lr);
    
    WritableCellFormat greyBackground = new WritableCellFormat();
    greyBackground.setWrap(true);
    greyBackground.setBackground(Colour.GRAY_50);
    lr = new Label(2, 19, "Grey background", greyBackground);
    s1.addCell(lr);

    WritableFont yellow = new WritableFont(WritableFont.ARIAL, 
                                           WritableFont.DEFAULT_POINT_SIZE,
                                           WritableFont.NO_BOLD,
                                           false,
                                           UnderlineStyle.NO_UNDERLINE,
                                           Colour.YELLOW);
    WritableCellFormat yellowOnBlue = new WritableCellFormat(yellow);
    yellowOnBlue.setWrap(true);
    yellowOnBlue.setBackground(Colour.BLUE);
    lr = new Label(2, 20, "Blue background, yellow foreground", yellowOnBlue);
    s1.addCell(lr);

    WritableCellFormat yellowOnBlack = new WritableCellFormat(yellow);
    yellowOnBlack.setWrap(true);
    yellowOnBlack.setBackground(Colour.PALETTE_BLACK);
    lr = new Label(3, 20, "Black background, yellow foreground",
                   yellowOnBlack);
    s1.addCell(lr);

    lr = new Label(0, 22, "Null label");
    s1.addCell(lr);

    lr = new Label(2, 22, null);
    s1.addCell(lr);

    lr = new Label(0, 24, 
                   "A very long label, more than 255 characters\012" +
                   "Rejoice O shores\012" +
                   "Sing O bells\012" + 
                   "But I with mournful tread\012" +
                   "Walk the deck my captain lies\012" +
                   "Fallen cold and dead\012"+
                   "Summer surprised, coming over the Starnbergersee\012" +
                   "With a shower of rain. We stopped in the Colonnade\012" +
                   "A very long label, more than 255 characters\012" +
                   "Rejoice O shores\012" +
                   "Sing O bells\012" + 
                   "But I with mournful tread\012" +
                   "Walk the deck my captain lies\012" +
                   "Fallen cold and dead\012"+
                   "Summer surprised, coming over the Starnbergersee\012" +
                   "With a shower of rain. We stopped in the Colonnade\012" +                   "A very long label, more than 255 characters\012" +
                   "Rejoice O shores\012" +
                   "Sing O bells\012" + 
                   "But I with mournful tread\012" +
                   "Walk the deck my captain lies\012" +
                   "Fallen cold and dead\012"+
                   "Summer surprised, coming over the Starnbergersee\012" +
                   "With a shower of rain. We stopped in the Colonnade\012" +                   "A very long label, more than 255 characters\012" +
                   "Rejoice O shores\012" +
                   "Sing O bells\012" + 
                   "But I with mournful tread\012" +
                   "Walk the deck my captain lies\012" +
                   "Fallen cold and dead\012"+
                   "Summer surprised, coming over the Starnbergersee\012" +
                   "With a shower of rain. We stopped in the Colonnade\012" +
                   "And sat and drank coffee an talked for an hour\012",
                   arial12format);
    s1.addCell(lr);

    WritableCellFormat vertical = new WritableCellFormat();
    vertical.setOrientation(Orientation.VERTICAL);
    lr = new Label(0, 26, "Vertical orientation", vertical);
    s1.addCell(lr);
    

    WritableCellFormat plus_90 = new WritableCellFormat();
    plus_90.setOrientation(Orientation.PLUS_90);
    lr = new Label(1, 26, "Plus 90", plus_90);
    s1.addCell(lr);


    WritableCellFormat minus_90 = new WritableCellFormat();
    minus_90.setOrientation(Orientation.MINUS_90);
    lr = new Label(2, 26, "Minus 90", minus_90);
    s1.addCell(lr);

    lr = new Label(0, 28, "Modified row height");
    s1.addCell(lr);
    s1.setRowView(28, 24*20);

    lr = new Label(0, 29, "Collapsed row");
    s1.addCell(lr);
    s1.setRowView(29, true);

    // Write hyperlinks
    try
    {
      Label l = new Label(0, 30, "Hyperlink to home page");
      s1.addCell(l);
      
      URL url = new URL("http://www.andykhan.com/jexcelapi");
      WritableHyperlink wh = new WritableHyperlink(0, 30, 8, 31, url);
      s1.addHyperlink(wh);

      // The below hyperlink clashes with above
      WritableHyperlink wh2 = new WritableHyperlink(7, 30, 9, 31, url);
      s1.addHyperlink(wh2);

      l = new Label(4, 2, "File hyperlink to documentation");
      s1.addCell(l);

      File file = new File("../jexcelapi/docs/index.html");
      wh = new WritableHyperlink(0, 32, 8, 32, file, 
                                 "JExcelApi Documentation");
      s1.addHyperlink(wh);

      // Add a hyperlink to another cell on this sheet
      wh = new WritableHyperlink(0, 34, 8, 34, 
                                 "Link to another cell",
                                 s1,
                                 0, 180, 1, 181);
      s1.addHyperlink(wh);

      file = new File("\\\\localhost\\file.txt");
      wh = new WritableHyperlink(0, 36, 8, 36, file);
      s1.addHyperlink(wh);

      // Add a very long hyperlink
      url = new URL("http://www.amazon.co.uk/exec/obidos/ASIN/0571058086"+
                   "/qid=1099836249/sr=1-3/ref=sr_1_11_3/202-6017285-1620664");
      wh = new WritableHyperlink(0, 38, 0, 38, url);
      s1.addHyperlink(wh);
    }
    catch (MalformedURLException e)
    {
      System.err.println(e.toString());
    }

    // Write out some merged cells
    Label l = new Label(5, 35, "Merged cells", timesBoldUnderline);
    s1.mergeCells(5, 35, 8, 37);
    s1.addCell(l);

    l = new Label(5, 38, "More merged cells");
    s1.addCell(l);
    Range r = s1.mergeCells(5, 38, 8, 41);
    s1.insertRow(40);
    s1.removeRow(39);
    s1.unmergeCells(r);

    // Merge cells and centre across them
    WritableCellFormat wcf = new WritableCellFormat();
    wcf.setAlignment(Alignment.CENTRE);
    l = new Label(5, 42, "Centred across merged cells", wcf);
    s1.addCell(l);
    s1.mergeCells(5, 42, 10, 42);

    wcf = new WritableCellFormat();
    wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
    wcf.setBackground(Colour.GRAY_25);
    l = new Label(3, 44, "Merged with border", wcf);
    s1.addCell(l);
    s1.mergeCells(3, 44, 4, 46);

    // Clash some ranges - the second range will not be added
    // Also merge some cells with two data items in the - the second data
    // item will not be merged
    /*
    l = new Label(5, 16, "merged cells");
    s1.addCell(l);

    Label l5 = new Label(7, 17, "this label won't appear");
    s1.addCell(l5);
    s1.mergeCells(5, 16, 8, 18);    

    s1.mergeCells(5, 19, 6, 24);
    s1.mergeCells(6, 18, 10, 19);
    */
    
    WritableFont courier10ptFont = new WritableFont(WritableFont.COURIER, 10);
    WritableCellFormat courier10pt = new WritableCellFormat(courier10ptFont);
    l = new Label(0, 49, "Courier fonts", courier10pt);
    s1.addCell(l);

    WritableFont tahoma12ptFont = new WritableFont(WritableFont.TAHOMA, 12);
    WritableCellFormat tahoma12pt = new WritableCellFormat(tahoma12ptFont);
    l = new Label(0, 50, "Tahoma fonts", tahoma12pt);
    s1.addCell(l);

    WritableFont.FontName wingdingsFont = 
      WritableFont.createFont("Wingdings 2");
    WritableFont wingdings210ptFont = new WritableFont(wingdingsFont, 10);
    WritableCellFormat wingdings210pt = new WritableCellFormat
      (wingdings210ptFont);
    l = new Label(0,51, "Bespoke Windgdings 2", wingdings210pt);
    s1.addCell(l);

    WritableCellFormat shrinkToFit = new WritableCellFormat(times12pt);
    shrinkToFit.setShrinkToFit(true);
    l = new Label(3,53, "Shrunk to fit", shrinkToFit);
    s1.addCell(l);

    l = new Label(3,55, "Some long wrapped text in a merged cell", 
                  arial12format);
    s1.addCell(l);
    s1.mergeCells(3,55,4,55);

    l = new Label(0, 57, "A cell with a comment");
    WritableCellFeatures cellFeatures = new WritableCellFeatures();
    cellFeatures.setComment("the cell comment");
    l.setCellFeatures(cellFeatures);
    s1.addCell(l);

    l = new Label(0, 59, 
                  "A cell with a long comment");
    cellFeatures = new WritableCellFeatures();
    cellFeatures.setComment("a very long cell comment indeed that won't " +
                            "fit inside a standard comment box, so a " +
                            "larger comment box is used instead",
                            5, 6);
    l.setCellFeatures(cellFeatures);
    s1.addCell(l);

    WritableCellFormat indented = new WritableCellFormat(times12pt);
    indented.setIndentation(4);
    l = new Label(0, 61, "Some indented text", indented);
    s1.addCell(l);

    l = new Label(0, 63, "Data validation:  list");
    s1.addCell(l);
    
    Blank b = new Blank(1,63);
    cellFeatures = new WritableCellFeatures();
    ArrayList al = new ArrayList();
    al.add("bagpuss");
    al.add("clangers");
    al.add("ivor the engine");
    al.add("noggin the nog");
    cellFeatures.setDataValidationList(al);
    b.setCellFeatures(cellFeatures);
    s1.addCell(b);

    l = new Label(0, 64, "Data validation:  number > 4.5");
    s1.addCell(l);
    
    b = new Blank(1,64);
    cellFeatures = new WritableCellFeatures();
    cellFeatures.setNumberValidation(4.5, WritableCellFeatures.GREATER_THAN);
    b.setCellFeatures(cellFeatures);
    s1.addCell(b);

    l = new Label(0, 65, "Data validation:  named range");
    s1.addCell(l);
    
    l = new Label(4, 65, "tiger");
    s1.addCell(l);
    l = new Label(5, 65, "sword");
    s1.addCell(l);
    l = new Label(6, 65, "honour");
    s1.addCell(l);
    l = new Label(7, 65, "company");
    s1.addCell(l);
    l = new Label(8, 65, "victory");
    s1.addCell(l);
    l = new Label(9, 65, "fortress");
    s1.addCell(l);

    b = new Blank(1,65);
    cellFeatures = new WritableCellFeatures();
    cellFeatures.setDataValidationRange("validation_range");
    b.setCellFeatures(cellFeatures);
    s1.addCell(b);

    // Set the row grouping
    s1.setRowGroup(39, 45, false);
    // s1.setRowGroup(72, 74, true);

    l = new Label(0, 66, "Block of cells B67-F71 with data validation");
    s1.addCell(l);

    al = new ArrayList();
    al.add("Achilles");
    al.add("Agamemnon");
    al.add("Hector");
    al.add("Odysseus");
    al.add("Patroclus");
    al.add("Nestor");

    b = new Blank(1, 66);
    cellFeatures = new WritableCellFeatures();
    cellFeatures.setDataValidationList(al);
    b.setCellFeatures(cellFeatures);
    s1.addCell(b);
    s1.applySharedDataValidation(b, 4,4);

    cellFeatures = new WritableCellFeatures();
    cellFeatures.setDataValidationRange("");
    l = new Label(0, 71, "Read only cell using empty data validation");
    l.setCellFeatures(cellFeatures);
    s1.addCell(l);

    // Set the row grouping
    s1.setRowGroup(39, 45, false);
    // s1.setRowGroup(72, 74, true);
  }
}








