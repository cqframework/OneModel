package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Locale.Category;

import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.biff.*;

/**
 * @author u0770443
 * 
 */
public class vMRTemplateGenerator
{
	/*
	 * !!!! IMPORTANT!  Make sure FINAL_CONTENT_ROW below is updated to last row in Excel spreadsheet of Source.  !!!!
	 */
	public static final int FINAL_CONTENT_ROW = 1316;
	
	private static final boolean INCLUDE_EXAMPLES = false;
	
	private static final boolean RESTRICT_OUTPUT_BASED_ON_COMPLETED_TEMPLATES_FILE = true; 
	// if false, everything available in source file will be outputted.  Can use for visualizing Google Doc contents for review purposes
	// if true, only those in the completed templates file will be outputted.  This is for publication and balloting purposes.
	
	private static final String COLUMN_TO_USE_FOR_TEMPLATES_OUTPUT = "D"; // if there is an X in this column, it is exported.  
	// Note if RESTRICT_OUTPUT_BASED_ON_COMPLETED_TEMPLATES_FILE is false, this does not come into play.		

	private enum TemplateCellFormat
	{
		heading, normal
	};

	private class CellContent
	{
		public int row;
		public String column;
		public String value;
		// public WritableCellFormat format;
		TemplateCellFormat format;

		public CellContent(int row, String column, String value, TemplateCellFormat format)
		{
			this.row = row;
			this.column = column;
			this.value = value;
			this.format = format;
		}
	}

	private String inputFile;
	private String completedTemplatesListFile;
	private static final String TABLE_OF_CONTENTS_OUTPUT_FILE_NAME = "_Table of Contents.xls";
	public static final int FIRST_CONTENT_ROW = 2;
	

	private int templateDescriptionRowLow, templateDescriptionColLow, templateDescriptionRowHigh, templateDescriptionColHigh;

	private TemplateCellFormat normalCell = TemplateCellFormat.normal;
	private TemplateCellFormat boldColoredCell = TemplateCellFormat.heading;

	private HashSet<String> myAlreadyUsedSaveFileNames = new HashSet<String>();
	private ArrayList<String> myCompletedTemplatesForExport = new ArrayList<String>();
	private HashSet<String> myExportedTemplates = new HashSet<String>();
	private HashSet<String> myTemplatesNotExportedBecauseNoDataElements = new HashSet<String>();

	public static final String TEMPLATES_FILE_PATH = "./"; // "\\\\smshscfs1.ad.utah.edu\\hscgroups\\Groups\\KMM ITS\\Projects\\HeD\\TemplatesFile\\";
	// maximum length for worksheets allowed by Excel
	private static final int MAXLENGTHOFSHEETNAME = 31;

	public vMRTemplateGenerator()
	{

	}

	public void setInputFile(String inputFile)
	{
		this.inputFile = inputFile;
	}

	public void setCompletedTemplatesListFile(String completesTemplatesListFile)
	{
		this.completedTemplatesListFile = completesTemplatesListFile;
	}

	public void process() throws IOException, WriteException
	{
		// get list of completed templates

		File completedTemplatesWorkbook = new File(completedTemplatesListFile);
		Workbook templatesWorkbook;
		
		if (RESTRICT_OUTPUT_BASED_ON_COMPLETED_TEMPLATES_FILE)
		{
			try
			{
				templatesWorkbook = Workbook.getWorkbook(completedTemplatesWorkbook);
	
				Sheet sheet = templatesWorkbook.getSheet(0);
	
				for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++)
				{
					String templateShortName = (sheet.getCell(0, rowIndex).getContents()).trim();
					String exportIndicator = (sheet.getCell(getColumnIndexFromLetter(COLUMN_TO_USE_FOR_TEMPLATES_OUTPUT), rowIndex).getContents()).trim();
					if ((exportIndicator.equals("X")) || (exportIndicator.equals("x")))
					{
						myCompletedTemplatesForExport.add(templateShortName);
					}					
				}
			}
			catch (BiffException e)
			{
				e.printStackTrace();
			}
		}
	
		File inputWorkbook = new File(inputFile);
		Workbook w;
		try
		{
			w = Workbook.getWorkbook(inputWorkbook);

			ArrayList<String> templateOIDs = new ArrayList<String>();
			HashMap<String, ArrayList<Integer>> templateOIDtoRowIndexes = new HashMap<String, ArrayList<Integer>>();
			HashMap<String, String> templateOIDtoShortNameMap = new HashMap<String, String>();
			HashMap<String, String> templateShortNameToOIDMap = new HashMap<String, String>();
			HashMap<String, String> templateOIDtoDescriptionMap = new HashMap<String, String>();

			ArrayList<String> templateCategories = new ArrayList<String>();
			HashMap<String, ArrayList<String>> templateCategoryToTemplateOIDs = new HashMap<String, ArrayList<String>>();
			HashMap<String, ArrayList<String>> templateCategoryToTemplateShortNames = new HashMap<String, ArrayList<String>>();

			Sheet sheet = w.getSheet(0);
			// collect template categories and list of contained template OIDs
			for (int rowIndex = FIRST_CONTENT_ROW - 1; rowIndex < FINAL_CONTENT_ROW - 1; rowIndex++)
			{
				String templateCategory = (sheet.getCell(3, rowIndex).getContents()).trim();
				
				/*
				String templateShortName = (sheet.getCell(6, rowIndex).getContents()).trim();
				if (templateShortName.startsWith("CDSInput"))
				{
					System.out.println("Template short name: [" + templateShortName + "]. Template category: [" + templateCategory + "].");
				}*/
				
				if (! templateCategory.equals(""))
				{
					if (templateCategory.equals("substance Administration"))
					{
						templateCategory = "Substance Administration";
					}
	
					String templateOIDwithinCategory = (sheet.getCell(4, rowIndex).getContents()).trim();
					String templateShortNameWithinCategory = (sheet.getCell(6, rowIndex).getContents()).trim();					
	
					ArrayList<String> templateOIDsWithinCategory = null;
					ArrayList<String> templateShortNamesWithinCategory = null;
	
					if ((! RESTRICT_OUTPUT_BASED_ON_COMPLETED_TEMPLATES_FILE) || (myCompletedTemplatesForExport.contains(templateShortNameWithinCategory)))
					{
						if (templateCategories.contains(templateCategory))
						{
							templateOIDsWithinCategory = templateCategoryToTemplateOIDs.get(templateCategory);
							templateShortNamesWithinCategory = templateCategoryToTemplateShortNames.get(templateCategory);
						}
						else
						{
							templateCategories.add(templateCategory);
							templateOIDsWithinCategory = new ArrayList<String>();
							templateShortNamesWithinCategory = new ArrayList<String>();
						}
						if (!templateOIDsWithinCategory.contains(templateOIDwithinCategory))
						{
							templateOIDsWithinCategory.add(templateOIDwithinCategory);
						}
						templateCategoryToTemplateOIDs.put(templateCategory, templateOIDsWithinCategory);
		
						if (!templateShortNamesWithinCategory.contains(templateShortNameWithinCategory))
						{
							templateShortNamesWithinCategory.add(templateShortNameWithinCategory);
						}
						templateCategoryToTemplateShortNames.put(templateCategory, templateShortNamesWithinCategory);
					}
				}
			}
						
			// collect template OIDs and the row indexes with their content
			for (int rowIndex = FIRST_CONTENT_ROW - 1; rowIndex < FINAL_CONTENT_ROW - 1; rowIndex++)
			{
				String templateOID = (sheet.getCell(4, rowIndex).getContents()).trim();
				String templateShortName = (sheet.getCell(6, rowIndex).getContents()).trim();
				String templateDescription = (sheet.getCell(12, rowIndex).getContents()).trim();
				templateOIDtoShortNameMap.put(templateOID, templateShortName);
				templateShortNameToOIDMap.put(templateShortName, templateOID);
				templateOIDtoDescriptionMap.put(templateOID, templateDescription);
				String dataElementStatus = (sheet.getCell(65, rowIndex).getContents()).trim();
				if (! dataElementStatus.equalsIgnoreCase("removed"))
				{
					if ((! RESTRICT_OUTPUT_BASED_ON_COMPLETED_TEMPLATES_FILE) || (myCompletedTemplatesForExport.contains(templateShortName)))
					{
						ArrayList<Integer> rowIndexes = null;
						if (templateOIDs.contains(templateOID))
						{
							rowIndexes = templateOIDtoRowIndexes.get(templateOID);
						}
						else
						{
							templateOIDs.add(templateOID);
							rowIndexes = new ArrayList<Integer>();
						}
						rowIndexes.add(new Integer(rowIndex));
						templateOIDtoRowIndexes.put(templateOID, rowIndexes);
					}
				}
			}

			Collections.sort(templateCategories);

			// process template category and its contained templates
			for (String templateCategory : templateCategories)
			{
				ArrayList<String> templateShortNamesWithinCategory = templateCategoryToTemplateShortNames.get(templateCategory);
				processTemplateCategory(templateCategory, templateShortNamesWithinCategory, templateOIDtoRowIndexes, sheet, templateShortNameToOIDMap);
			}

			// create and write out table of contents

			// Category Template Short Name OID Description
			ArrayList<CellContent> tableOfContentsCellContentList = new ArrayList<CellContent>();

			tableOfContentsCellContentList.add(new CellContent(1, "A", "Category", boldColoredCell));
			tableOfContentsCellContentList.add(new CellContent(1, "B", "Template Short Name", boldColoredCell));
			tableOfContentsCellContentList.add(new CellContent(1, "C", "OID", boldColoredCell));
			tableOfContentsCellContentList.add(new CellContent(1, "D", "Description", boldColoredCell));

			int rowNumber = 2;

			for (String templateCategory : templateCategories)
			{
				//ArrayList<String> templateOIDsWithinCategory = templateCategoryToTemplateOIDs.get(templateCategory);
				ArrayList<String> templateShortNamesWithinCategory = templateCategoryToTemplateShortNames.get(templateCategory);
				Collections.sort(templateShortNamesWithinCategory);

				for (String shortName : templateShortNamesWithinCategory)
				{
					String templateOID = templateShortNameToOIDMap.get(shortName);
					String description = templateOIDtoDescriptionMap.get(templateOID);
					tableOfContentsCellContentList.add(new CellContent(rowNumber, "A", templateCategory, normalCell));
					tableOfContentsCellContentList.add(new CellContent(rowNumber, "B", shortName, normalCell));
					tableOfContentsCellContentList.add(new CellContent(rowNumber, "C", templateOID, normalCell));
					tableOfContentsCellContentList.add(new CellContent(rowNumber, "D", description, normalCell));
					rowNumber++;
				}
			}
			writeTOCExcelOutput(TEMPLATES_FILE_PATH + TABLE_OF_CONTENTS_OUTPUT_FILE_NAME, "Table of Contents", tableOfContentsCellContentList);

			// write out summary of what was processed and what was not
			System.out.println();
			System.out.println("--- Template Export Summary ---");
			System.out.println("Total # of templates marked completed and ready for export: " + myCompletedTemplatesForExport.size());

			ArrayList<String> templatesMarkedReadyForExportAndExported = new ArrayList<String>();
			ArrayList<String> templatesMarkedReadyForExportAndNotExportedBecauseNoDataElements = new ArrayList<String>();
			ArrayList<String> templatesMarkedReadyForExportAndNotExportedForUnknownReason = new ArrayList<String>();
			ArrayList<String> templatesExportedEvenThoughNotMarkedReadyForExport = new ArrayList<String>();

			Collections.sort(myCompletedTemplatesForExport);

			for (String completedTemplateForExport : myCompletedTemplatesForExport)
			{
				if (myExportedTemplates.contains(completedTemplateForExport))
				{
					templatesMarkedReadyForExportAndExported.add(completedTemplateForExport);
				}
				else if (myTemplatesNotExportedBecauseNoDataElements.contains(completedTemplateForExport))
				{
					templatesMarkedReadyForExportAndNotExportedBecauseNoDataElements.add(completedTemplateForExport);
				}
				else
				{
					templatesMarkedReadyForExportAndNotExportedForUnknownReason.add(completedTemplateForExport);
				}
			}

			for (String exportedTemplate : myExportedTemplates)
			{
				if (!myCompletedTemplatesForExport.contains(exportedTemplate))
				{
					templatesExportedEvenThoughNotMarkedReadyForExport.add(exportedTemplate);
				}
			}
			
			if (RESTRICT_OUTPUT_BASED_ON_COMPLETED_TEMPLATES_FILE)
			{
				System.out.println("# of templates marked completed and ready for export, and exported: "
						+ templatesMarkedReadyForExportAndExported.size());
	
				System.out.println("# of templates marked completed and ready for export, and not exported because no data elements: "
						+ templatesMarkedReadyForExportAndNotExportedBecauseNoDataElements.size());
				for (String templateNotExportedBecauseNoDataElement : templatesMarkedReadyForExportAndNotExportedBecauseNoDataElements)
				{
					System.out.println("   - " + templateNotExportedBecauseNoDataElement);
				}
	
				System.out.println("# of templates marked completed and ready for export, and not exported for unknown reason: "
						+ templatesMarkedReadyForExportAndNotExportedForUnknownReason.size());
				for (String templateNotExportedForUnknownReason : templatesMarkedReadyForExportAndNotExportedForUnknownReason)
				{
					System.out.println("   - " + templateNotExportedForUnknownReason);
				}
	
				System.out.println("# of templates exported, but not marked completed and ready for export: "
						+ templatesExportedEvenThoughNotMarkedReadyForExport.size());
				for (String templateExportedEvenThoughNotMarkedReadyForExport : templatesExportedEvenThoughNotMarkedReadyForExport)
				{
					System.out.println("   - " + templateExportedEvenThoughNotMarkedReadyForExport);
				}
			}
		}
		catch (BiffException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 * @throws WriteException
	 */
	private void processTemplateCategory(String templateCategory, ArrayList<String> templateShortNamessWithinCategory,
			HashMap<String, ArrayList<Integer>> templateOIDtoRowIndexes, Sheet sheet, HashMap<String, String> templateShortNameToOIDMap)
			throws WriteException, IOException
	{
		//System.out.println("Processing template category: " + templateCategory);

		// file name substitutions
		if (templateCategory.equals("vMR Extended Datatypes"))
		{
			templateCategory = "vMR Extended Type";
		}
		else if (templateCategory.equals("Adverse Events"))
		{
			templateCategory = "Adverse Event";
		}
		else if (templateCategory.equals("Encounters"))
		{
			templateCategory = "Encounter";
		}
		else if (templateCategory.equals("Entities"))
		{
			templateCategory = "Entity";
		}
		else if (templateCategory.equals("Goals"))
		{
			templateCategory = "Goal";
		}
		else if (templateCategory.equals("Observations"))
		{
			templateCategory = "Observation";
		}
		else if (templateCategory.equals("Problems"))
		{
			templateCategory = "Problem";
		}
		else if (templateCategory.equals("Procedures"))
		{
			templateCategory = "Procedure";
		}

		String fileName = TEMPLATES_FILE_PATH + templateCategory + " Templates.xls";
		ArrayList<String> sheetNames = new ArrayList<String>();
		ArrayList<ArrayList<CellContent>> sheetContents = new ArrayList<ArrayList<CellContent>>();

		// process template
		int counter = 1;
		HashMap<String, Integer> sheetNameToCount = new HashMap<String, Integer>();

		Collections.sort(templateShortNamessWithinCategory);

		for (String templateShortName : templateShortNamessWithinCategory)
		{
			String templateOID = templateShortNameToOIDMap.get(templateShortName);
			ArrayList<Integer> rowIndexes = templateOIDtoRowIndexes.get(templateOID);
			// String sheetName = templateName; // gets into trouble with long
			// names
			// String sheetName = "TMPL" + counter;
			String sheetName = templateShortName;			
			if (sheetName.length() > MAXLENGTHOFSHEETNAME)
			{
				Integer count = sheetNameToCount.get(sheetName);
				if (count == null)
				{
					sheetName = sheetName.substring(0, MAXLENGTHOFSHEETNAME - 3) + "~";
					sheetNameToCount.put(sheetName, new Integer(1));
				}
				else
				{
					sheetName = sheetName.substring(0, MAXLENGTHOFSHEETNAME - 3) + "~" + count;
					sheetNameToCount.put(sheetName, new Integer(1 + count));
				}
			}
			HashSet<String> templateOIDs = new HashSet<String>(templateOIDtoRowIndexes.keySet());
			//System.out.println(templateOIDs);
			ArrayList<CellContent> templateSheetContents = getTemplateSheetContents(templateOID, rowIndexes, sheet, templateOIDs);
			if (templateSheetContents != null)
			{
				sheetNames.add(sheetName);
				sheetContents.add(templateSheetContents);
				counter++;
			}
		}

		for (String alreadyUsedFileName : myAlreadyUsedSaveFileNames)
		{
			if (alreadyUsedFileName.equalsIgnoreCase(fileName))
			{
				System.err.println("**ERROR: template categories with only case differences exist.");
				System.err.println("** " + alreadyUsedFileName + " will be OVERWRITTEN by " + fileName);
			}
		}

		myAlreadyUsedSaveFileNames.add(fileName);

		writeExcelOutput(fileName, sheetNames, sheetContents);
	}

	/**
	 * Returns null if template does not contain any data elements and should
	 * not be exported.
	 * 
	 * @param templateOID
	 * @param rowIndexes
	 * @param sheet
	 * @return
	 */
	private ArrayList<CellContent> getTemplateSheetContents(String templateOID, ArrayList<Integer> rowIndexes, Sheet sheet, HashSet<String> templateOIDs)
	{
		ArrayList<CellContent> cellContentsToReturn = new ArrayList<CellContent>();

		int firstRowIndex = rowIndexes.get(0).intValue();

		// collect all template-level metadata
		String priority = (sheet.getCell(0, firstRowIndex).getContents()).trim();
		String completionPriorityForBallot = (sheet.getCell(1, firstRowIndex).getContents()).trim();
		String hl7BallotSubmissionDateString = (sheet.getCell(2, firstRowIndex).getContents()).trim();
		String templateCategory = (sheet.getCell(3, firstRowIndex).getContents()).trim();
		// String templateOID = (sheet.getCell(4, firstRowIndex).getContents()).trim();
		String templateLongName = (sheet.getCell(5, firstRowIndex).getContents()).trim();
		String templateInternalName = (sheet.getCell(6, firstRowIndex).getContents()).trim();
		String effectiveDate = (sheet.getCell(7, firstRowIndex).getContents()).trim();
		String status = (sheet.getCell(8, firstRowIndex).getContents()).trim();
		String version = (sheet.getCell(9, firstRowIndex).getContents()).trim();
		String expirationDate = (sheet.getCell(10, firstRowIndex).getContents()).trim();
		String templateCategoryOIDdeprecated = (sheet.getCell(11, firstRowIndex).getContents()).trim();
		String description = (sheet.getCell(12, firstRowIndex).getContents()).trim();
		String developerComments = (sheet.getCell(13, firstRowIndex).getContents()).trim();
		String dataRestrictionYN = (sheet.getCell(14, firstRowIndex).getContents()).trim();
		String dataContentExpectationsIfNoRestriction = (sheet.getCell(15, firstRowIndex).getContents()).trim();
		String dataElementsWhereCodeRestrictinAllowed = (sheet.getCell(16, firstRowIndex).getContents()).trim();
		String primaryDateTimeElementSubjectToRestriction = (sheet.getCell(17, firstRowIndex).getContents()).trim();
		String searchBackPeriodRestrictionAllowed = (sheet.getCell(18, firstRowIndex).getContents()).trim();
		String dateSearchBackPeriod = (sheet.getCell(19, firstRowIndex).getContents()).trim();
		String searchFowardPeriodRestrictionAllowed = (sheet.getCell(20, firstRowIndex).getContents()).trim();
		String numberLookBackPeriodRestrictionAllowed = (sheet.getCell(21, firstRowIndex).getContents()).trim();
		String numberLookBack = (sheet.getCell(22, firstRowIndex).getContents()).trim();
		String maxSamplingRateRestrictionAllowed = (sheet.getCell(23, firstRowIndex).getContents()).trim();
		String maxSamplingRate = (sheet.getCell(24, firstRowIndex).getContents()).trim();
		String example1 = (sheet.getCell(25, firstRowIndex).getContents()).trim();
		String example2 = (sheet.getCell(26, firstRowIndex).getContents()).trim();

		String rootVmrClass = (sheet.getCell(41, firstRowIndex).getContents()).trim();
		/*
		 * String templateCategory = sheet.getCell(0,
		 * firstRowIndex).getContents(); String templateLongName =
		 * sheet.getCell(2, firstRowIndex).getContents(); String
		 * templateInternalName = sheet.getCell(3, firstRowIndex)
		 * .getContents(); String effectiveDate = sheet.getCell(4,
		 * firstRowIndex).getContents(); String status = sheet.getCell(5,
		 * firstRowIndex).getContents(); String version = sheet.getCell(6,
		 * firstRowIndex).getContents(); String expirationDate =
		 * sheet.getCell(7, firstRowIndex).getContents(); String
		 * templateCategoryOIDdeprecated = sheet.getCell(8, firstRowIndex)
		 * .getContents(); String description = sheet.getCell(9,
		 * firstRowIndex).getContents(); String dataRestrictionYN =
		 * sheet.getCell(10, firstRowIndex) .getContents(); String
		 * dataContentExpectationsIfNoRestriction = sheet.getCell(11,
		 * firstRowIndex).getContents(); String
		 * dataElementsWhereCodeRestrictinAllowed = sheet.getCell(12,
		 * firstRowIndex).getContents(); String
		 * primaryDateTimeElementSubjectToRestriction = sheet.getCell(13,
		 * firstRowIndex).getContents(); String
		 * searchBackPeriodRestrictionAllowed = sheet.getCell(14,
		 * firstRowIndex).getContents(); String dateSearchBackPeriod =
		 * sheet.getCell(15, firstRowIndex) .getContents(); String
		 * searchFowardPeriodRestrictionAllowed = sheet.getCell(16,
		 * firstRowIndex).getContents(); String
		 * numberLookBackPeriodRestrictionAllowed = sheet.getCell(17,
		 * firstRowIndex).getContents(); String numberLookBack =
		 * sheet.getCell(18, firstRowIndex).getContents(); String
		 * maxSamplingRateRestrictionAllowed = sheet.getCell(19,
		 * firstRowIndex).getContents(); String maxSamplingRate =
		 * sheet.getCell(20, firstRowIndex).getContents(); String example1 =
		 * sheet.getCell(21, firstRowIndex).getContents(); String example2 =
		 * sheet.getCell(22, firstRowIndex).getContents(); String rootVmrClass =
		 * sheet.getCell(25, firstRowIndex).getContents();
		 */
		// specify labels
		int rowNum = 1;
		cellContentsToReturn.add(new CellContent(rowNum, "A", "", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "B", "", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "C", "", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "D", "", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "E", "", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "F", "", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "H", "", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "I", "", boldColoredCell));

		cellContentsToReturn.add(new CellContent(rowNum++, "G", "Template Description", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "G", description, normalCell));
		templateDescriptionRowLow = rowNum;
		templateDescriptionColLow = getColumnIndexFromLetter("G");

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Template OID", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", templateOID, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Template Short Name", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", templateInternalName, normalCell));
		
		cellContentsToReturn.add(new CellContent(rowNum, "A", "Template Long Name", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", templateLongName, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Root vMR Class", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", rootVmrClass, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Effective Date", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", effectiveDate, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Status Code", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", status, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Version Label", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", version, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Expiration Date", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", expirationDate, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Data Restriction Allowed", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", dataRestrictionYN, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Data Expected if No Restriction", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", dataContentExpectationsIfNoRestriction, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Code Restriction Elements", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", dataElementsWhereCodeRestrictinAllowed, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "DateTime Restriction Element", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", primaryDateTimeElementSubjectToRestriction, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Search Back Period Restriction", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", searchBackPeriodRestrictionAllowed, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Search Forward Period Restriction", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", searchFowardPeriodRestrictionAllowed, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Number Look Back Restriction", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum++, "B", numberLookBackPeriodRestrictionAllowed, normalCell));

		cellContentsToReturn.add(new CellContent(rowNum, "A", "Max Sampling Rate Restriction", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "B", maxSamplingRateRestrictionAllowed, normalCell));
		//
		templateDescriptionRowHigh = rowNum;
		templateDescriptionColHigh = getColumnIndexFromLetter("E");

		// skip a row - create a blank row
		rowNum += 2;
		cellContentsToReturn.add(new CellContent(rowNum, "A", "Data Element Name", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "B", "vMR Data Element", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "C", "Cardinality", boldColoredCell));		
		cellContentsToReturn.add(new CellContent(rowNum, "D", "Mandatory", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "E", "Conformance", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "F", "Fixed Value", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "G", "Ad-Hoc", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "H", "vMR Data Type", boldColoredCell));
		// cellContentsToReturn.add(new CellContent(rowNum, "F",
		// "Value Restriction", boldColoredCell));
		cellContentsToReturn.add(new CellContent(rowNum, "I", "Constraints", boldColoredCell));

		cellContentsToReturn.add(new CellContent(rowNum, "J", "Comments", boldColoredCell));

		// enter content

		// int rowNumber = ++rowNum;
		// iterate through elements
		rowNum++;
		boolean noDataElementLevelData = false;

		for (Integer rowIndex : rowIndexes)
		{
			int rowIndexInt = rowIndex.intValue();
			String businessFriendlyDataElementName = (sheet.getCell(40, rowIndex).getContents()).trim();
			String vmrDataElement = (sheet.getCell(42, rowIndex).getContents()).trim();
			String cardinality = (sheet.getCell(43, rowIndex).getContents()).trim();
			String optionality = (sheet.getCell(44, rowIndex).getContents()).trim();
			String fixedValue = (sheet.getCell(45, rowIndex).getContents()).trim();
			String adHoc = (sheet.getCell(46, rowIndex).getContents()).trim();
			String vmrDataType = (sheet.getCell(47, rowIndex).getContents()).trim();
			String constraints = (sheet.getCell(48, rowIndex).getContents()).trim();
			if ((constraints.toUpperCase().contains("MARK TO IDENTIFY")) || 
				(constraints.toUpperCase().contains("ROB TO IDENTIFY")) || 
				((constraints.toUpperCase().contains("MARK")) && (constraints.toUpperCase().contains("IDENTIF"))) ||
				((constraints.toUpperCase().contains("ROB")) && (! constraints.toUpperCase().contains("MICROBI")) && (! constraints.toUpperCase().contains("PROBLEM")) && (constraints.toUpperCase().contains("IDENTIF"))))										
			{
				System.err.println("> Noting TBD for following constraints note: " + constraints);
				System.err.println("  Template " + templateInternalName + " in template category " + templateCategory
						+ ".  Data element: " + businessFriendlyDataElementName);
				constraints = "Constraints TBD";
				
			}		
			
			// check to see if an OID is being referenced in a constraints section that does NOT exist
			String templateOIDReferencedInConstraints = getTemplateOIDReferencedInConstraints(constraints);
			if (templateOIDReferencedInConstraints != null)
			{
				if (! templateOIDs.contains(templateOIDReferencedInConstraints))
				{
					System.err.println("> Template OID " + templateOIDReferencedInConstraints + " is referenced by " + templateCategory + "." + templateInternalName + "." + businessFriendlyDataElementName + " but does not exist.");
				}
			}
			
			String comments = (sheet.getCell(49, rowIndex).getContents()).trim();
			String dataElementStatus = (sheet.getCell(65, rowIndex).getContents()).trim();

			// manipulate data
			if (
					(vmrDataType.equals("CD") && constraints.equals("")) 
					|| constraints.equals("TBD") 
					|| constraints.equals("Constraints TBD")
			   )
			{
				constraints = "Constraints TBD";
				System.out.println("> Constraints TBD for " + templateCategory + "." + templateInternalName + "." + businessFriendlyDataElementName);
				
			}

			// verify data exists

			if (businessFriendlyDataElementName.equals("") && vmrDataElement.equals("") && cardinality.equals("") && optionality.equals("")
					&& vmrDataType.equals("") && constraints.equals("") && comments.equals(""))
			{
				noDataElementLevelData = true;
				System.out.println("> No data element level data for template " + templateInternalName + " in template category " + templateCategory
						+ ".  Not including.");
				myTemplatesNotExportedBecauseNoDataElements.add(templateInternalName);
			}
			else
			{
				myExportedTemplates.add(templateInternalName);
			}
			
			if (dataElementStatus.equalsIgnoreCase("removed"))
			{
				// don't add
			}
			else if (dataElementStatus.equalsIgnoreCase("")) // expected
			{
				cellContentsToReturn.add(new CellContent(rowNum, "A", businessFriendlyDataElementName, normalCell));
				cellContentsToReturn.add(new CellContent(rowNum, "B", vmrDataElement, normalCell));
				
				// normalize optionality for use later
				if ((optionality.equals("C")) || (optionality.equals("RE")) || (optionality.equals("RE, C")) || (optionality.equals("RE,C"))
						|| (optionality.equals("C, RE")) || (optionality.equals("C,RE")) || (optionality.equals("")))
				{
					// expected values
				}
				else
				{
					System.err.println("> Optionaity of [" + optionality + "] is unexpected for " + templateCategory + "." + templateInternalName + "." + businessFriendlyDataElementName + ".  Check source and correct issues."); 					
				}
				
				// cardinality
				// normalize
				if ((cardinality.equals("[0..*]")) || (cardinality.equals("0.*")))
				{
					cardinality = "0..*";
				}
				else if ((cardinality.equals("0..*")) || (cardinality.equals("0..1")) || (cardinality.equals("1..*")) || (cardinality.equals("1..1")))
				{
					// expected values
				}
				else
				{
					System.err.println("> Cardinality of [" + cardinality + "] is unexpected for " + templateCategory + "." + templateInternalName + "." + businessFriendlyDataElementName + ".  Check source and correct issues."); 					
				}
				cellContentsToReturn.add(new CellContent(rowNum, "C", cardinality, normalCell));
				
				// mandatory				
				String mandatory;
				if (cardinality.startsWith("1.."))
				{
					mandatory = "Y";
				}
				else
				{
					mandatory = "N";
				}
				cellContentsToReturn.add(new CellContent(rowNum, "D", mandatory, normalCell));
				
				// conformance
				String conformance = "";
				if (cardinality.startsWith("1.."))
				{
					conformance = "R";
				}
				else
				{
					if ((adHoc.equals("Y")) || (optionality.contains("C")) || (optionality.contains("RE")))
					{
						conformance = "R";
					}					
				}
				
				cellContentsToReturn.add(new CellContent(rowNum, "E", conformance, normalCell));
				
				cellContentsToReturn.add(new CellContent(rowNum, "F", fixedValue, normalCell));
				cellContentsToReturn.add(new CellContent(rowNum, "G", adHoc, normalCell));
								
				cellContentsToReturn.add(new CellContent(rowNum, "H", vmrDataType, normalCell));
				cellContentsToReturn.add(new CellContent(rowNum, "I", constraints, normalCell));
				cellContentsToReturn.add(new CellContent(rowNum, "J", comments, normalCell));
			}
			else
			{
				// status not expected
				System.err.println("> Not adding data element " + businessFriendlyDataElementName + " for template " + templateInternalName + " due to unexpected data element status of " + dataElementStatus + ".");
			}
						
			rowNum++;
		}
		rowNum++;
		
		if (INCLUDE_EXAMPLES)
		{
			if (example2.equals(""))
			{
				cellContentsToReturn.add(new CellContent(rowNum, "A", "Example", boldColoredCell));
				cellContentsToReturn.add(new CellContent(rowNum++, "B", example1, normalCell));						
			}
			else
			{
				cellContentsToReturn.add(new CellContent(rowNum, "A", "Example 1", boldColoredCell));
				cellContentsToReturn.add(new CellContent(rowNum++, "B", example1, normalCell));
				rowNum++;
				cellContentsToReturn.add(new CellContent(rowNum, "A", "Example 2", boldColoredCell));
				cellContentsToReturn.add(new CellContent(rowNum++, "B", example2, normalCell));
			}
		}
		

		if (noDataElementLevelData)
		{
			cellContentsToReturn = null;
		}

		return cellContentsToReturn;
	}
	
	/**
	 * Returns String of a template OID contained in the constraints string.
	 * Identifies the String that starts with 2.16.840.1.113883.3.1829 (the vMR root OID) and end in a number followed by 
	 * something other than a number or period.
	 * 
	 * Returns null if no match.
	 * @return
	 */
	private String getTemplateOIDReferencedInConstraints(String constraints)
	{
		int startIndex = constraints.indexOf("2.16.840.1.113883.3.1829");
		
		StringBuffer stringToReturn = new StringBuffer();
		stringToReturn.append("2.16.840.1.113883.3.1829");
		boolean lastStrWasPeriod = false;
		for (int k = startIndex + 24; k < constraints.length(); k++)
		{
			String nextStr = new String(constraints.substring(k, k + 1));
			
			if ((nextStr.equals("0")) || (nextStr.equals("1")) || (nextStr.equals("2")) || (nextStr.equals("3"))  || (nextStr.equals("4"))  || (nextStr.equals("5"))  || (nextStr.equals("6"))  || (nextStr.equals("7"))  || (nextStr.equals("8"))  || (nextStr.equals("9"))  || (nextStr.equals(".")))
			{
				if (nextStr.equals("."))
				{
					lastStrWasPeriod = true;
				}
				else
				{
					lastStrWasPeriod = false;					
				}
				stringToReturn.append(nextStr);
			}
			else
			{
				break;
			}			
		}
		
		String templateOID = stringToReturn.toString();
		if (lastStrWasPeriod)
		{
			templateOID = templateOID.substring(0, templateOID.length() -1);
		}
		
		if (! templateOID.equals("2.16.840.1.113883.3.1829"))
		{
			return templateOID;
		}
		return null;		
	}

	/**
	 * sheet names and sheet contents must match in terms of order and number
	 * 
	 * @param fileName
	 * @param sheetNames
	 * @param sheetContents
	 * @throws IOException
	 * @throws WriteException
	 */
	private void writeExcelOutput(String fileName, ArrayList<String> sheetNames, ArrayList<ArrayList<CellContent>> sheetContents) throws IOException,
			WriteException
	{
		File file = new File(fileName);
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setSuppressWarnings(true);		

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);

		WritableFont times10pt = new WritableFont(WritableFont.ARIAL, 10);
		WritableCellFormat normalCellFormat = new WritableCellFormat(times10pt);
		normalCellFormat.setWrap(true);
		try
		{
			normalCellFormat.setWrap(true);
			normalCellFormat.setVerticalAlignment(VerticalAlignment.TOP);

		}
		catch (WriteException e)
		{
			e.printStackTrace();
		}

		WritableFont times10ptBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
		WritableCellFormat boldColoredCellFormat = new WritableCellFormat(times10ptBold);
		boldColoredCellFormat.setWrap(true);
		try
		{
			boldColoredCellFormat.setBackground(Colour.GREY_25_PERCENT);
		}
		catch (WriteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CellView cf = new CellView();
		cf.setAutosize(true);
		// ws.setColumnView(5, cf);

		ArrayList<Integer> rowsWithExamples = new ArrayList<Integer>();

		double rowHeightPerTextLine = 13.5;
		int sheetNum = 0;

		for (String sheetName : sheetNames)
		{
			workbook.createSheet(sheetName, sheetNum);
			ArrayList<CellContent> sheetContent = sheetContents.get(sheetNum);
			//System.out.println("Writing sheet for: " + sheetName);

			WritableSheet excelSheet = workbook.getSheet(sheetNum);

			for (int rowNum = 1; rowNum < 18; rowNum++)
			{
				excelSheet.mergeCells(1, rowNum, 5, rowNum);				
			}
			excelSheet.mergeCells(6, 0, 9, 0);
			excelSheet.mergeCells(6, 17, 9, 17);
			excelSheet.mergeCells(6, 1, 9, 16);

			for (CellContent cellContent : sheetContent)
			{
				int column = getColumnIndexFromLetter(cellContent.column);
				int row = cellContent.row - 1;
				WritableCellFormat format;
				if (cellContent.format == TemplateCellFormat.normal)
					format = normalCellFormat;
				else
					format = boldColoredCellFormat;

				// add extra space for rows if top-area contents contain > 50
				// characters
				if ((column == 1) && (row >= 1) && (row <= 16))
				{

					int cellContentStringLen = cellContent.value.length();
					int numRowsNeeded = (cellContentStringLen / 50) + 1;
					excelSheet.setRowView(row, (int) Math.rint(Math.ceil(20 * rowHeightPerTextLine * numRowsNeeded)));
				}

				if (cellContent.value.startsWith("Example"))
				{
					excelSheet.mergeCells(1, row, 9, row);
				}

				if (cellContent.value.startsWith("<"))
				{
					String cellValue = cellContent.value;
					int rowHeight = (int) Math.rint(Math.ceil(20 * rowHeightPerTextLine * (countNewLines(cellValue) + 1.0)));
					excelSheet.setRowView(row, rowHeight);
				}
				Label label = new Label(column, row, cellContent.value, format);

				excelSheet.addCell(label);
			}

			// for (int col=0; col < 8; col++) excelSheet.setColumnView(col,
			// cf);
			//excelSheet.mergeCells(templateDescriptionColLow, templateDescriptionRowLow - 2, templateDescriptionColLow, templateDescriptionRowLow - 2);
			//excelSheet.mergeCells(templateDescriptionColLow, templateDescriptionRowLow - 1, templateDescriptionColHigh + 2,	templateDescriptionRowHigh - 1);

			/*
			 * for (Integer rowWithExample : rowsWithExamples) {
			 * excelSheet.mergeCells(1, rowWithExample.intValue(), 6,
			 * rowWithExample.intValue()); }
			 */

			excelSheet.setColumnView(0, 32);
			excelSheet.setColumnView(1, 18);
			excelSheet.setColumnView(2, 7);
			excelSheet.setColumnView(3, 7);
			excelSheet.setColumnView(4, 7);
			excelSheet.setColumnView(5, 7);
			excelSheet.setColumnView(6, 7);
			excelSheet.setColumnView(7, 15);
			excelSheet.setColumnView(8, 40);
			excelSheet.setColumnView(9, 30);
			// excelSheet.setColumnView(7, 20);
			sheetNum++;
		}

		workbook.write();
		workbook.close();
	}

	/**
	 * Special case for table of contents
	 * 
	 * @param fileName
	 * @param sheetName
	 * @param sheetContents
	 * @throws IOException
	 * @throws WriteException
	 */
	private void writeTOCExcelOutput(String fileName, String sheetName, ArrayList<CellContent> sheetContent) throws IOException, WriteException
	{
		File file = new File(fileName);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);

		WritableFont times10pt = new WritableFont(WritableFont.ARIAL, 10);
		WritableCellFormat normalCellFormat = new WritableCellFormat(times10pt);
		normalCellFormat.setWrap(true);
		try
		{
			normalCellFormat.setWrap(true);
			normalCellFormat.setVerticalAlignment(VerticalAlignment.TOP);

		}
		catch (WriteException e)
		{
			e.printStackTrace();
		}

		WritableFont times10ptBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
		WritableCellFormat boldColoredCellFormat = new WritableCellFormat(times10ptBold);
		boldColoredCellFormat.setWrap(true);
		try
		{
			boldColoredCellFormat.setBackground(Colour.GREY_25_PERCENT);
		}
		catch (WriteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CellView cf = new CellView();
		cf.setAutosize(true);
		// ws.setColumnView(5, cf);

		ArrayList<Integer> rowsWithExamples = new ArrayList<Integer>();

		double rowHeightPerTextLine = 13.5;
		int sheetNum = 0;

		workbook.createSheet(sheetName, sheetNum);

		System.out.println("Writing sheet for: " + sheetName);

		WritableSheet excelSheet = workbook.getSheet(sheetNum);
		for (CellContent cellContent : sheetContent)
		{
			int column = getColumnIndexFromLetter(cellContent.column);
			WritableCellFormat format;
			if (cellContent.format == TemplateCellFormat.normal)
				format = normalCellFormat;
			else
				format = boldColoredCellFormat;

			if (cellContent.value.startsWith("Example"))
			{
				excelSheet.mergeCells(1, cellContent.row - 1, 6, cellContent.row - 1);
			}
			if (cellContent.value.startsWith("<"))
			{
				String cellValue = cellContent.value;
				int rowHeight = (int) Math.rint(Math.ceil(20 * rowHeightPerTextLine * (countNewLines(cellValue) + 1.0)));
				excelSheet.setRowView(cellContent.row - 1, rowHeight);
			}
			Label label = new Label(column, cellContent.row - 1, cellContent.value, format);

			excelSheet.addCell(label);
		}

		excelSheet.setColumnView(0, 20);
		excelSheet.setColumnView(1, 30);
		excelSheet.setColumnView(2, 33);
		excelSheet.setColumnView(3, 80);

		workbook.write();
		workbook.close();
	}

	public static int countNewLines(String source)
	{
		int count = 0;
		for (int i = 0; i < source.length(); i++)
		{
			if ((source.charAt(i) == '\n') || (source.charAt(i) == '\r'))
			{
				count++;
			}
		}
		return count;
	}

	/**
	 * Covers A-H
	 * 
	 * @param columnLetter
	 * @return
	 */
	private int getColumnIndexFromLetter(String columnLetter)
	{
		if (columnLetter.equals("A"))
		{
			return 0;
		}
		else if (columnLetter.equals("B"))
		{
			return 1;
		}
		else if (columnLetter.equals("C"))
		{
			return 2;
		}
		else if (columnLetter.equals("D"))
		{
			return 3;
		}
		else if (columnLetter.equals("E"))
		{
			return 4;
		}
		else if (columnLetter.equals("F"))
		{
			return 5;
		}
		else if (columnLetter.equals("G"))
		{
			return 6;
		}
		else if (columnLetter.equals("H"))
		{
			return 7;
		}
		else if (columnLetter.equals("I"))
		{
			return 8;
		}
		else if (columnLetter.equals("J"))
		{
			return 9;
		}
		else if (columnLetter.equals("K"))
		{
			return 10;
		}
		else
		{
			System.err.println("> getColumnIndexFromLetter received unexpected input >= L");
			return 11;
		}
	}

	public static void main(String[] args) throws IOException, WriteException
	{
		vMRTemplateGenerator generator = new vMRTemplateGenerator();
		// test.setInputFile(TEMPLATES_FILE_PATH +
		// "00--Source - Transformed.xls");
		generator.setInputFile(TEMPLATES_FILE_PATH + "00--Source.xls");
		generator.setCompletedTemplatesListFile(TEMPLATES_FILE_PATH + "00--CompletedTemplatesList.xls");
		generator.process();
	}
}