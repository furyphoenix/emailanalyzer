package neos.algorithm.lofia;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.StringTokenizer;

/**
 * Set of utilities to support various Association Rule Mining (ARM)
 * algorithms.
 * <p></p>
 * Source code for LoFIA is developed based on
 * Coenen, F. (2003), The LUCS-KDD FP-growth Association Rule Mining Algorithm,
 * <p>
 * "http://www.cxc.liv.ac.uk/~frans/KDD/Software/FPgrowth/fpGrowth.html",
 * <p>
 * Department of Computer Science, The University of Liverpool, UK.
 * @author Le Minh Nghia, NTU-Singapore
 *
 */
public class AssocRuleMining {

    /** Maximum confidence value */
    private static final double MAX_CONFIDENCE = 100.0;

    /** Maximum support value */
    private static final double MAX_SUPPORT = 100.0;

    /** Maximum confidence value */
    private static final double MIN_CONFIDENCE = 0.0;

    // Constants

    /** Minimum support value */
    private static final double MIN_SUPPORT = 0.0;

    /** Command line argument for % confidence (default = 80%). */
    protected double confidence = 80.0;

    /**
     * 2-D array used to renumber columns for input data in terms of
     * frequency of single attributes (reordering will enhance performance
     * for some ARM algorithms). 
     */
    protected int[][] conversionArray = null;

    /**
     * 2-D aray to hold input data from data file. Note that within the data
     * array records are numbered from zero, thus rexcord one has index 0 etc. 
     */
    protected short[][] dataArray = null;

    // Command line arguments with default values and associated fields.

    /** Command line argument for data file name. */
    protected String fileName = null;

    /** The file path */
    protected File filePath = null;

    /**
     * Minimum support value in terms of number of rows. <P>Set when input
     * data is read and the number of records is known,   
     */
    protected double minSupport = 0;

    /** Command line argument for number of columns. */
    protected int numCols = 0;

    /** The number of one itemsets (singletons). */
    protected int numOneItemSets = 0;

    /** Command line argument for number of rows. */
    protected int numRows = 0;

    /**
     * 1-D array used to reconvert input data column numbers to their
     * original numbering where the input data has been ordered to enhance
     * computational efficiency. 
     */
    protected short[] reconversionArray = null;

    // Data structures

    /** The reference to start of the rule list. */
    protected RuleNode startRulelist = null;

    /** Command line argument for % support (default = 20%). */
    protected double support = 20.0;

    /**
     * Flag to indicate whether input data has been sorted and pruned or
     * not. 
     */
    protected boolean isPrunedFlag = false;

    /** Flag to indicate whether input data has been sorted or not. */
    protected boolean isOrderedFlag = false;

    /** Input format OK flag( default = <TT>true</TT>). */
    protected boolean inputFormatOkFlag = true;

    /** Flag to indicate whether system has data or not. */
    private boolean haveDataFlag = false;

    // Flags

    /**
     * Error flag used when checking command line arguments (default =
     * <TT>true</TT>). 
     */
    protected boolean errorFlag = true;

    // Other fields

    /** The input stream. */
    protected BufferedReader fileInput;

    /* ------ CONSTRUCTORS ------ */

    /** Processes command line arguments */
    public AssocRuleMining(String[] args) {

        // Process command line arguments
        for (int index = 0; index < args.length; index++) {
            idArgument(args[index]);
        }

        // If command line arguments read successfully (errorFlag set to "true")
        // check validity of arguments
        if (errorFlag) {
            CheckInputArguments();
        } else {
            outputMenu();
        }
    }

    /* ------ METHODS ------ */
    /* ---------------------------------------------------------------- */
    /*  */
    /* COMMAND LINE ARGUMENTS */
    /*  */
    /* ---------------------------------------------------------------- */
    /* IDENTIFY ARGUMENT */

    /**
     * Identifies nature of individual command line agruments:
     * -C = confidence, -F = file name, -S = support. 
     */
    protected void idArgument(String argument) {
        if (argument.charAt(0) == '-') {
            char flag = argument.charAt(1);

            argument = argument.substring(2, argument.length());

            switch (flag) {
            case 'C' :
                confidence = Double.parseDouble(argument);

                break;

            case 'F' :
                fileName = argument;

                break;

            case 'S' :
                support = Double.parseDouble(argument);

                break;

            default :
                System.out.println("INPUT ERROR: Unrecognise command " + "line  argument -" + flag + argument);
                errorFlag = false;
            }
        } else {
            System.out.println("INPUT ERROR: All command line arguments " + "must commence with a '-' character ("
                               + argument + ")");
            errorFlag = false;
        }
    }

    /* CHECK INPUT ARGUMENTS */

    /**
     * Invokes methods to check values associate with command line
     * arguments 
     */
    protected void CheckInputArguments() {

        // Check support and confidence input
        checkSupportAndConfidence();

        // Check file name
        checkFileName();

        // Return
        if (errorFlag) {
            outputSettings();
        } else {
            outputMenu();
        }
    }

    /* CHECK SUPPORT AND CONFIDANCE */

    /**
     * Checks support and confidence input % values, if either is out of
     * bounds then <TT>errorFlag</TT> set to <TT>false</TT>. 
     */
    protected void checkSupportAndConfidence() {

        // Check Support
        if ((support < MIN_SUPPORT) || (support > MAX_SUPPORT)) {
            System.out.println("INPUT ERROR: Support must be specified " + "as a percentage (" + MIN_SUPPORT + " - "
                               + MAX_SUPPORT + ")");
            errorFlag = false;
        }

        // Check confidence
        if ((confidence < MIN_CONFIDENCE) || (confidence > MAX_CONFIDENCE)) {
            System.out.println("INPUT ERROR: Confidence must be " + "specified as a percentage (" + MIN_CONFIDENCE
                               + " - " + MAX_CONFIDENCE + ")");
            errorFlag = false;
        }
    }

    /* CHECK FILE NAME */

    /**
     * Checks if data file name provided, if not <TT>errorFlag</TT> set
     * to <TT>false</TT>. 
     */
    protected void checkFileName() {
        if (fileName == null) {
            System.out.println("INPUT ERROR: Must specify file name (-F)");
            errorFlag = false;
        }
    }

    /* ---------------------------------------------------------------- */
    /*  */
    /* READ INPUT DATA FROM FILE */
    /*  */
    /* ---------------------------------------------------------------- */
    /* INPUT DATA SET */

    /** Commences process of getting input data (GUI version also exists). */
    public void inputDataSet() {

        // Read the file
        readFile();

        // Check ordering (only if input format is OK)
        if (inputFormatOkFlag) {
            if (checkOrdering()) {
                System.out.println("Number of records = " + numRows);
                countNumCols();
                System.out.println("Number of columns = " + numCols);
                minSupport = (numRows * support) / 100.0;
                System.out.println("Min support       = " + twoDecPlaces(minSupport) + " (records)");
            } else {
                System.out.println("Error reading file: " + fileName + "\n");
                closeFile();
                System.exit(1);
            }
        }
    }

    /* READ FILE */

    /**
     * Reads input data from file specified in command line argument
     * <TT>fileName</TT>. <P>Note that it is assumed
     * that no empty records are included. Proceeds as follows:
     * <OL>
     * <LI>Gets number of rows (lines) in file, checking format of each line
     * (space separated integers), if incorrectly formatted line found
     * <TT>inputFormatOkFlag</TT> set to <TT>false</TT>.
     * <LI>Dimensions input array.
     * <LI>Reads data
     * </OL> 
     */
    protected void readFile() {
        try {

            // Dimension data structure
            inputFormatOkFlag = true;
            numRows           = getNumberOfLines(fileName);

            if (inputFormatOkFlag) {
                dataArray = new short[numRows][];

                // Read file
                System.out.println("Reading input file: " + fileName);
                readInputDataSet();
            } else {
                System.out.println("Error reading file: " + fileName + "\n");
            }
        } catch (IOException ioException) {
            System.out.println("Error reading File");
            closeFile();
            System.exit(1);
        }
    }

    /* GET NUMBER OF LINES */

    /**
     * Gets number of lines/records in input file and checks format
     * of each line.
     * @param nameOfFile the filename of the file to be opened.
     * @return the number of rows in the given file.
     */
    protected int getNumberOfLines(String nameOfFile) throws IOException {
        int counter = 0;

        // Open the file
        if (filePath == null) {
            openFileName(nameOfFile);
        } else {
            openFilePath();
        }

        // Loop through file incrementing counter
        // get first row.
        String line = fileInput.readLine();

        while (line != null) {
            checkLine(counter + 1, line);

            StringTokenizer dataLine       = new StringTokenizer(line);
            int             numberOfTokens = dataLine.countTokens();

            if (numberOfTokens == 0) {
                break;
            }

            counter++;
            line = fileInput.readLine();
        }

        // Close file and return
        closeFile();

        return (counter);
    }

    /* CHECK LINE */

    /**
     * Check whether given line from input file is of appropriate format
     * (space separated integers), if incorrectly formatted line found
     * <TT>inputFormatOkFlag</TT> set to <TT>false</TT>.
     * @param counter the line number in the input file.
     * @param str the current line from the input file. 
     */
    protected void checkLine(int counter, String str) {
        for (int index = 0; index < str.length(); index++) {
            if (!Character.isDigit(str.charAt(index)) &&!Character.isWhitespace(str.charAt(index))) {
                System.err.println("FILE INPUT ERROR:\n" + "charcater on line " + counter
                                   + " is not a digit or white space");
                inputFormatOkFlag = false;
                haveDataFlag      = false;

                break;
            }
        }
    }

    /* READ INPUT DATA SET */

    /** Reads input data from file specified in command line argument. */
    public void readInputDataSet() throws IOException {
        readInputDataSet(fileName);
    }

    /* READ INPUT DATA SET */

    /**
     * Reads input data from given file.
     * @param fName the given file name.  
     */
    protected void readInputDataSet(String fName) throws IOException {
        int rowIndex = 0;

        // Open the file
        if (filePath == null) {
            openFileName(fName);
        } else {
            openFilePath();
        }

        // Get first row.
        String line = fileInput.readLine();

        // Preocess rest of file
        while (line != null) {

            // Process line
            if (!processInputLine(line, rowIndex)) {
                break;
            }

            // Increment first (row) index in 2-D data array
            rowIndex++;

            // get next line
            line = fileInput.readLine();
        }

        // Close file
        closeFile();
    }

    /* READ INPUT DATA SEGMENT */

    /**
     * Reads input data segment from a given file and places content into to
     * the data array structure commencing at the given row index, continues until
     * the end index is rerached.
     * @param fName the given file name.
     * @param startRowIndex the given row strat index.
     * @param endRowIndex the given row end index. 
     */
    protected void readInputDataSetSeg(String fName, int startRowIndex, int endRowIndex) throws IOException {
        int rowIndex = startRowIndex;

        // Open the file
        if (filePath == null) {
            openFileName(fName);
        } else {
            openFilePath();
        }

        // get first row.
        String line = fileInput.readLine();

        for (int index = startRowIndex; index < endRowIndex; index++) {

            // Process line
            processInputLine(line, index);

            // get next line
            line = fileInput.readLine();
        }

        // Close file
        closeFile();
    }

    /* PROCESS INPUT LINE */

    /**
     * Processes a line from the input file and places it in the
     * <TT>dataArray</TT> structure.
     * @param line the line to be processed from the input file
     * @param rowIndex the index to the current location in the
     * <TT>dataArray</TT> structure.
     * @rerturn true if successfull, false if empty record. 
     */
    private boolean processInputLine(String line, int rowIndex) {

        // If no line return false
        if (line == null) {
            return (false);
        }

        // Tokenise line
        StringTokenizer dataLine       = new StringTokenizer(line);
        int             numberOfTokens = dataLine.countTokens();

        // Empty line or end of file found, return false
        if (numberOfTokens == 0) {
            return (false);
        }

        // Convert input string to a sequence of short integers
        short[] code = binConversion(dataLine, numberOfTokens);

        // Dimension row in 2-D dataArray
        int codeLength = code.length;

        dataArray[rowIndex] = new short[codeLength];

        // Assign to elements in row
        for (int colIndex = 0; colIndex < codeLength; colIndex++) {
            dataArray[rowIndex][colIndex] = code[colIndex];
        }

        // Return
        return (true);
    }

    /* CHECK DATASET ORDERING */

    /**
     * Checks that data set is ordered correctly.
     * @return true if appropriate ordering, false otherwise. 
     */
    protected boolean checkOrdering() {
        boolean result = true;

        // Loop through input data
        for (int index = 0; index < dataArray.length; index++) {
            if (!checkLineOrdering(index + 1, dataArray[index])) {
                haveDataFlag = false;
                result       = false;
            }
        }

        // Return
        return (result);
    }

    /* CHECK LINE ORDERING */

    /**
     * Checks whether a given line in the input data is in numeric sequence.
     * @param lineNum the line number.
     * @param itemSet the item set represented by the line
     * @return true if OK and false otherwise. 
     */
    protected boolean checkLineOrdering(int lineNum, short[] itemSet) {
        for (int index = 0; index < itemSet.length - 1; index++) {
            if (itemSet[index] >= itemSet[index + 1]) {
                System.err.println("FILE FORMAT ERROR:\n" + "Attribute data in line " + lineNum
                                   + " not in numeric order");

                return (false);
            }
        }

        // Default return
        return (true);
    }

    /* COUNT NUMBER OF COLUMNS */

    /** Counts number of columns represented by input data. */
    protected void countNumCols() {
        int maxAttribute = 0;

        // Loop through data array
        for (int index = 0; index < dataArray.length; index++) {
            int lastIndex = dataArray[index].length - 1;

            if (dataArray[index][lastIndex] > maxAttribute) {
                maxAttribute = dataArray[index][lastIndex];
            }
        }

        numCols        = maxAttribute;
        numOneItemSets = numCols;    // default value only
    }

    /* OPEN FILE NAME */

    /**
     * Opens input file using fileName (instance field).
     * @param nameOfFile the filename of the file to be opened. 
     */
    protected void openFileName(String nameOfFile) {
        try {

            // Open file
            FileReader file = new FileReader(nameOfFile);

            fileInput = new BufferedReader(file);
        } catch (IOException ioException) {
            System.err.println("Error Opening File");
            System.exit(1);
        }
    }

    /* OPEN FILE PATH */

    /** Opens file using filePath (instance field). */
    protected void openFilePath() {
        try {

            // Open file
            FileReader file = new FileReader(filePath);

            fileInput = new BufferedReader(file);
        } catch (IOException ioException) {
            System.err.println("Error Opening File");
            System.exit(1);
        }
    }

    /* CLOSE FILE */

    /** Close file fileName (instance field). */
    protected void closeFile() {
        if (fileInput != null) {
            try {
                fileInput.close();
            } catch (IOException ioException) {
                System.err.println("Error Closing File");
                System.exit(1);
            }
        }
    }

    /* BINARY CONVERSION. */

    /**
     * Produce an item set (array of elements) from input
     * line.
     * @param dataLine row from the input data file
     * @param numberOfTokens number of items in row
     * @return 1-D array of short integers representing attributes in input
     * row 
     */
    protected short[] binConversion(StringTokenizer dataLine, int numberOfTokens) {
        short   number;
        short[] newItemSet = null;

        // Load array
        for (int tokenCounter = 0; tokenCounter < numberOfTokens; tokenCounter++) {
            number     = new Short(dataLine.nextToken()).shortValue();
            newItemSet = realloc1(newItemSet, number);
        }

        // Return itemSet
        return (newItemSet);
    }

    /* ---------------------------------------------------------------- */
    /*  */
    /* REORDER DATA SET ACCORDING TO ATTRIBUTE FREQUENCY */
    /*  */
    /* ---------------------------------------------------------------- */
    /* REORDER INPUT DATA: */

    /**
     * Reorders input data according to frequency of
     * single attributes. <P> Example, given the data set:
     * <PRE>
     * 1 2 5
     * 1 2 3
     * 2 4 5
     * 1 2 5
     * 2 3 5
     * </PRE>
     * This would produce a countArray (ignore index 0):
     * <PRE>
     * +---+---+---+---+---+---+
     * |   | 1 | 2 | 3 | 4 | 5 |
     * +---+---+---+---+---+---+
     * |   | 3 | 5 | 2 | 1 | 4 |
     * +---+---+---+---+---+---+
     * </PRE>
     * Which sorts to:
     * <PRE>
     * +---+---+---+---+---+---+
     * |   | 2 | 5 | 1 | 3 | 4 |
     * +---+---+---+---+---+---+
     * |   | 5 | 4 | 3 | 2 | 1 |
     * +---+---+---+---+---+---+
     * </PRE>
     * Giving rise to the conversion Array of the form (no index 0):
     * <PRE>
     * +---+---+---+---+---+---+
     * |   | 3 | 1 | 4 | 5 | 2 |
     * +---+---+---+---+---+---+
     * |   | 3 | 5 | 2 | 1 | 4 |
     * +---+---+---+---+---+---+
     * </PRE>
     * Note that the second row here are the counts which no longer play a role
     * in the conversion exercise. Thus to the new column number for column 1 is
     * column 3 (i.e. the first vale at index 1). The reconversion array of the
     * form:
     * <PRE>
     * +---+---+---+---+---+---+
     * |   | 2 | 5 | 1 | 3 | 4 |
     * +---+---+---+---+---+---+
     * </PRE> 
     */
    public void idInputDataOrdering() {

        // Count singles and store in countArray;
        int[][] countArray = countSingles();

        // Bubble sort count array on support value (second index)
        orderCountArray(countArray);

        // Define conversion and reconversion arrays
        defConvertArrays(countArray);

        // Set sorted flag
        isOrderedFlag = true;
    }

    /* COUNT SINGLES */

    /**
     * Counts number of occurrences of each single attribute in the
     * input data.
     * @return 2-D array where first row represents column numbers
     * and second row represents support counts. 
     */
    protected int[][] countSingles() {

        // Dimension and initialize count array
        int[][] countArray = new int[numCols + 1][2];

        for (int index = 0; index < countArray.length; index++) {
            countArray[index][0] = index;
            countArray[index][1] = 0;
        }

        // Step through input data array counting singles and incrementing
        // appropriate element in the count array
        for (int rowIndex = 0; rowIndex < dataArray.length; rowIndex++) {
            if (dataArray[rowIndex] != null) {
                for (int colIndex = 0; colIndex < dataArray[rowIndex].length; colIndex++) {
                    countArray[dataArray[rowIndex][colIndex]][1]++;
                }
            }
        }

        // Return
        return (countArray);
    }

    /* ORDER COUNT ARRAY */

    /**
     * Bubble sorts count array produced by <TT>countSingles</TT> method
     * so that array is ordered according to frequency of single items.
     * @param countArray The 2-D array returned by the <TT>countSingles</TT>
     * method. 
     */
    private void orderCountArray(int[][] countArray) {
        int     attribute, quantity;
        boolean isOrdered;
        int     index;

        do {
            isOrdered = true;
            index     = 1;

            while (index < (countArray.length - 1)) {
                if (countArray[index][1] >= countArray[index + 1][1]) {
                    index++;
                } else {
                    isOrdered = false;

                    // Swap
                    attribute                = countArray[index][0];
                    quantity                 = countArray[index][1];
                    countArray[index][0]     = countArray[index + 1][0];
                    countArray[index][1]     = countArray[index + 1][1];
                    countArray[index + 1][0] = attribute;
                    countArray[index + 1][1] = quantity;

                    // Increment index
                    index++;
                }
            }
        } while (isOrdered == false);
    }

    /* ORDER FIRST N ELEMENTS IN COUNT ARRAY */

    /**
     * Bubble sorts first N elements in count array produced by
     * <TT>countSingles</TT> method so that array is ordered according to
     * frequency of single items. <P> Used when ordering classification input
     * data.
     * @param countArray The 2-D array returned by the <TT>countSingles</TT>
     * method.
     * @param endIndex the index of the Nth element. 
     */
    protected void orderFirstNofCountArray(int[][] countArray, int endIndex) {
        int     attribute, quantity;
        boolean isOrdered;
        int     index;

        do {
            isOrdered = true;
            index     = 1;

            while (index < endIndex) {
                if (countArray[index][1] >= countArray[index + 1][1]) {
                    index++;
                } else {
                    isOrdered = false;

                    // Swap
                    attribute                = countArray[index][0];
                    quantity                 = countArray[index][1];
                    countArray[index][0]     = countArray[index + 1][0];
                    countArray[index][1]     = countArray[index + 1][1];
                    countArray[index + 1][0] = attribute;
                    countArray[index + 1][1] = quantity;

                    // Increment index
                    index++;
                }
            }
        } while (isOrdered == false);
    }

    /* DEFINE CONVERSION ARRAYS: */

    /**
     * Defines conversion and reconversion arrays.
     * @param countArray The 2-D array sorted by the <TT>orderCcountArray</TT>
     * method.
     */
    protected void defConvertArrays(int[][] countArray) {

        // Dimension arrays
        conversionArray   = new int[numCols + 1][2];
        reconversionArray = new short[numCols + 1];

        // Assign values
        for (int index = 1; index < countArray.length; index++) {
            conversionArray[countArray[index][0]][0] = index;
            conversionArray[countArray[index][0]][1] = countArray[index][1];
            reconversionArray[index]                 = (short) countArray[index][0];
        }

        // Diagnostic ouput if desired
        // outputConversionArrays();
    }

    /* RECAST INPUT DATA. */

    /**
     * Recasts the contents of the data array so that each record is ordered
     * according to conversion array.
     * <P>Proceed as follows:
     * <p>
     * 1) For each record in the data array. Create an empty new itemSet array.
     * <p>
     * 2) Place into this array attribute/column numbers that correspond to the
     *  appropriate equivalents contained in the conversion array.
     * <p>
     * 3) Reorder this itemSet and return into the data array. 
     */
    public void recastInputData() {
        short[] itemSet;
        int     attribute;

        // Step through data array using loop construct
        for (int rowIndex = 0; rowIndex < dataArray.length; rowIndex++) {
            itemSet = new short[dataArray[rowIndex].length];

            // For each element in the itemSet replace with attribute number
            // from conversion array
            for (int colIndex = 0; colIndex < dataArray[rowIndex].length; colIndex++) {
                attribute         = dataArray[rowIndex][colIndex];
                itemSet[colIndex] = (short) conversionArray[attribute][0];
            }

            // Sort itemSet and return to data array
            sortItemSet(itemSet);
            dataArray[rowIndex] = itemSet;
        }
    }

    /* RECAST INPUT DATA AND REMOVE UNSUPPORTED SINGLE ATTRIBUTES. */

    /**
     * Recasts the contents of the data array so that each record is
     * ordered according to ColumnCounts array and excludes non-supported
     * elements. <P> Proceed as follows:
     *   <P>
     * 1) For each record in the data array. Create an empty new itemSet array.
     * <P>
     * 2) Place into this array any column numbers in record that are
     *  supported at the index contained in the conversion array.
     * <P>
     * 3) Assign new itemSet back into to data array 
     */
    public void recastInputDataAndPruneUnsupportedAtts() {
        short[] itemSet;
        int     attribute;

        // Step through data array using loop construct
        for (int rowIndex = 0; rowIndex < dataArray.length; rowIndex++) {

            // Check for empty row
            if (dataArray[rowIndex] != null) {
                itemSet = null;

                // For each element in the current record find if supported with
                // reference to the conversion array. If so add to "itemSet".
                for (int colIndex = 0; colIndex < dataArray[rowIndex].length; colIndex++) {
                    attribute = dataArray[rowIndex][colIndex];

                    // Check support
                    if (conversionArray[attribute][1] >= minSupport) {
                        itemSet = reallocInsert(itemSet, (short) conversionArray[attribute][0]);
                    }
                }

                // Return new item set to data array
                dataArray[rowIndex] = itemSet;
            }
        }

        // Set isPrunedFlag (used with GUI interface)
        isPrunedFlag = true;

        // Reset number of one item sets field
        numOneItemSets = getNumSupOneItemSets();
    }

    /* GET NUM OF SUPPORTE ONE ITEM SETS */

    /**
     * Gets number of supported single item sets (note this is not necessarily
     * the same as the number of columns/attributes in the input set).
     * @return Number of supported 1-item sets 
     */
    protected int getNumSupOneItemSets() {
        int counter = 0;

        // Step through conversion array incrementing counter for each
        // supported element found
        for (int index = 1; index < conversionArray.length; index++) {
            if (conversionArray[index][1] >= minSupport) {
                counter++;
            }
        }

        // Return
        return (counter);
    }

    /* RESIZE INPUT DATA */

    /**
     * Recasts the input data sets so that only N percent is used.
     * @param percentage the percentage of the current input data that is to form
     * the new input data set (number between 0 and 100). 
     */
    public void resizeInputData(double percentage) {

        // Redefine number of rows
        numRows = (int) ((double) numRows * (percentage / 100.0));
        System.out.println("Recast input data, new num rows = " + numRows);

        // Dimension and populate training set.
        short[][] trainingSet = new short[numRows][];

        for (int index = 0; index < numRows; index++) {
            trainingSet[index] = dataArray[index];
        }

        // Assign training set label to input data set label.
        dataArray = trainingSet;

        // Determine new minimum support threshold value
        minSupport = (numRows * support) / 100.0;
    }

    /**
     * Reconverts given item set according to contents of reconversion array.
     * @param itemSet The fgiven itemset.
     * @return The reconverted itemset. 
     */
    protected short[] reconvertItemSet(short[] itemSet) {

        // If no conversion return orginal item set
        if (reconversionArray == null) {
            return (itemSet);
        }

        // If item set null return null
        if (itemSet == null) {
            return (null);
        }

        // Define new item set
        short[] newItemSet = new short[itemSet.length];

        // Copy
        for (int index = 0; index < newItemSet.length; index++) {
            newItemSet[index] = reconversionArray[itemSet[index]];
        }

        // Return
        return (newItemSet);
    }

    /**
     * Reconvert single item if appropriate.
     * @param item the given item (attribute).
     * @return the reconvered item. 
     */
    protected short reconvertItem(short item) {

        // If no conversion return orginal item
        if (reconversionArray == null) {
            return (item);
        }

        // Otherwise return reconvert item
        return (reconversionArray[item]);
    }

    /* -------------------------------------------------------------- */
    /*  */
    /* RULE LINKED LIST ORDERED ACCORDING TO CONFIDENCE */
    /*  */
    /* -------------------------------------------------------------- */

    /*
     *  Methods for inserting rules into a linked list of rules ordered
     * according to confidence (most confident first). Each rule described in
     * terms of 3 fields: 1) Antecedent (an item set), 2) a consequent (an item
     * set), 3) a confidence value (double). <P> The support field is not used.
     */

    /*
     *  INSERT (ASSOCIATION/CLASSIFICATION) RULE INTO RULE LINKED LIST (ORDERED
     * ACCORDING CONFIDENCE).
     */

    /**
     * Inserts an (association/classification) rule into the linkedlist of
     * rules pointed at by <TT>startRulelist</TT>. <P> The list is ordered so that
     * rules with highest confidence are listed first. If two rules have the same
     * confidence the new rule will be placed after the existing rule. Thus, if
     * using an Apriori approach to generating rules, more general rules will
     * appear first in the list with more specific rules (i.e. rules with a larger
     * antecedent) appearing later as the more general rules will be generated
     * first.
     * @param antecedent the antecedent (LHS) of the rule.
     * @param consequent the consequent (RHS) of the rule.
     * @param confidenceForRule the associated confidence value.  
     */
    protected void insertRuleintoRulelist(short[] antecedent, short[] consequent, double confidenceForRule) {

        // Create new node
        RuleNode newNode = new RuleNode(antecedent, consequent, confidenceForRule);

        // Empty list situation
        if (startRulelist == null) {
            startRulelist = newNode;

            return;
        }

        // Add new node to start
        if (confidenceForRule > startRulelist.confidenceForRule) {
            newNode.next  = startRulelist;
            startRulelist = newNode;

            return;
        }

        // Add new node to middle
        RuleNode markerNode   = startRulelist;
        RuleNode linkRuleNode = startRulelist.next;

        while (linkRuleNode != null) {
            if (confidenceForRule > linkRuleNode.confidenceForRule) {
                markerNode.next = newNode;
                newNode.next    = linkRuleNode;

                return;
            }

            markerNode   = linkRuleNode;
            linkRuleNode = linkRuleNode.next;
        }

        // Add new node to end
        markerNode.next = newNode;
    }

    /* ----------------------------------------------- */
    /*  */
    /* ITEM SET INSERT AND ADD METHODS */
    /*  */
    /* ----------------------------------------------- */
    /* REALLOC INSERT */

    /**
     * Resizes given item set so that its length is increased by one
     * and new element inserted.
     * @param oldItemSet the original item set
     * @param newElement the new element/attribute to be inserted
     * @return the combined item set 
     */
    protected short[] reallocInsert(short[] oldItemSet, short newElement) {

        // No old item set
        if (oldItemSet == null) {
            short[] newItemSet = { newElement };

            return (newItemSet);
        }

        // Otherwise create new item set with length one greater than old
        // item set
        int     oldItemSetLength = oldItemSet.length;
        short[] newItemSet       = new short[oldItemSetLength + 1];

        // Loop
        int index1;

        for (index1 = 0; index1 < oldItemSetLength; index1++) {
            if (newElement < oldItemSet[index1]) {
                newItemSet[index1] = newElement;

                // Add rest
                for (int index2 = index1 + 1; index2 < newItemSet.length; index2++) {
                    newItemSet[index2] = oldItemSet[index2 - 1];
                }

                return (newItemSet);
            } else {
                newItemSet[index1] = oldItemSet[index1];
            }
        }

        // Add to end
        newItemSet[newItemSet.length - 1] = newElement;

        // Return new item set
        return (newItemSet);
    }

    /* REALLOC 1 */

    /**
     * Resizes given item set so that its length is increased by one
     * and appends new element (identical to append method)
     * @param oldItemSet the original item set
     * @param newElement the new element/attribute to be appended
     * @return the combined item set 
     */
    protected short[] realloc1(short[] oldItemSet, short newElement) {

        // No old item set
        if (oldItemSet == null) {
            short[] newItemSet = { newElement };

            return (newItemSet);
        }

        // Otherwise create new item set with length one greater than old
        // item set
        int     oldItemSetLength = oldItemSet.length;
        short[] newItemSet       = new short[oldItemSetLength + 1];

        // Loop
        int index;

        for (index = 0; index < oldItemSetLength; index++) {
            newItemSet[index] = oldItemSet[index];
        }

        newItemSet[index] = newElement;

        // Return new item set
        return (newItemSet);
    }

    /* REALLOC 2 */

    /**
     * Resizes given array so that its length is increased by one element
     * and new element added to front
     * @param oldItemSet the original item set
     * @param newElement the new element/attribute to be appended
     * @return the combined item set 
     */
    protected short[] realloc2(short[] oldItemSet, short newElement) {

        // No old array
        if (oldItemSet == null) {
            short[] newItemSet = { newElement };

            return (newItemSet);
        }

        // Otherwise create new array with length one greater than old array
        int     oldItemSetLength = oldItemSet.length;
        short[] newItemSet       = new short[oldItemSetLength + 1];

        // Loop
        newItemSet[0] = newElement;

        for (int index = 0; index < oldItemSetLength; index++) {
            newItemSet[index + 1] = oldItemSet[index];
        }

        // Return new array
        return (newItemSet);
    }

    /* --------------------------------------------- */
    /*  */
    /* ITEM SET DELETE METHODS */
    /*  */
    /* --------------------------------------------- */
    /* REMOVE ELEMENT N */

    /**
     * Removes the nth element/attribute from the given item set.
     * @param oldItemSet the given item set.
     * @param n the index of the element to be removed (first index is 0).
     * @return Revised item set with nth element removed. 
     */
    protected short[] removeElementN(short[] oldItemSet, int n) {
        if (oldItemSet.length <= n) {
            return (oldItemSet);
        } else {
            short[] newItemSet = new short[oldItemSet.length - 1];

            for (int index = 0; index < n; index++) {
                newItemSet[index] = oldItemSet[index];
            }

            for (int index = n + 1; index < oldItemSet.length; index++) {
                newItemSet[index - 1] = oldItemSet[index];
            }

            return (newItemSet);
        }
    }

    /* ---------------------------------------------------------------- */
    /*  */
    /* METHODS TO RETURN SUBSETS OF ITEMSETS */
    /*  */
    /* ---------------------------------------------------------------- */
    /* COMPLEMENT */

    /**
     * Returns complement of first itemset with respect to second itemset.
     * @param itemSet1 the first given item set.
     * @param itemSet2 the second given item set.
     * @return complement if <TT>itemSet1</TT> in <TT>itemSet2</TT>. 
     */
    protected short[] complement(short[] itemSet1, short[] itemSet2) {
        int lengthOfComp = itemSet2.length - itemSet1.length;

        // Return null if no complement
        if (lengthOfComp < 1) {
            return (null);
        }

        // Otherwsise define combination array and determine complement
        short[] complement      = new short[lengthOfComp];
        int     complementIndex = 0;

        for (int index = 0; index < itemSet2.length; index++) {

            // Add to combination if not in first itemset
            if (notMemberOf(itemSet2[index], itemSet1)) {
                complement[complementIndex] = itemSet2[index];
                complementIndex++;
            }
        }

        // Return
        return (complement);
    }

    /* --------------------------------------- */
    /*  */
    /* SORT ITEM SET */
    /*  */
    /* --------------------------------------- */
    /* SORT ITEM SET: Given an unordered itemSet, sort the set */

    /**
     * Sorts an unordered item set.
     * @param itemSet the given item set. 
     */
    protected void sortItemSet(short[] itemSet) {
        short   temp;
        boolean isOrdered;
        int     index;

        do {
            isOrdered = true;
            index     = 0;

            while (index < (itemSet.length - 1)) {
                if (itemSet[index] <= itemSet[index + 1]) {
                    index++;
                } else {
                    isOrdered = false;

                    // Swap
                    temp               = itemSet[index];
                    itemSet[index]     = itemSet[index + 1];
                    itemSet[index + 1] = temp;

                    // Increment index
                    index++;
                }
            }
        } while (isOrdered == false);
    }

    /* ----------------------------------------------------- */
    /*  */
    /* BOOLEAN ITEM SET METHODS ETC. */
    /*  */
    /* ----------------------------------------------------- */
    /* NOT MEMBER OF */

    /**
     * Checks whether a particular element/attribute identified by a
     * column number is not a member of the given item set.
     * @param number the attribute identifier (column number).
     * @param itemSet the given item set.
     * @return true if first argument is not a member of itemSet, and false
     * otherwise 
     */
    protected boolean notMemberOf(short number, short[] itemSet) {

        // Loop through itemSet
        for (int index = 0; index < itemSet.length; index++) {
            if (number < itemSet[index]) {
                return (true);
            }

            if (number == itemSet[index]) {
                return (false);
            }
        }

        // Got to the end of itemSet and found nothing, return true
        return (true);
    }

    /* -------------------------------------------------- */
    /*  */
    /* ITEM SET COMBINATIONS */
    /*  */
    /* -------------------------------------------------- */
    /* COMBINATIONS */

    /**
     * Invokes <TT>combinations</TT> method to calculate all possible
     * combinations of a given item set. <P>
     * For example given the item set [1,2,3] this will result in the
     * combinations[[1],[2],[3],[1,2],[1,3],[2,3],[1,2,3]].
     * @param inputSet the given item set.
     * @return array of arrays representing all possible combinations (may be null
     * if no combinations). 
     */
    protected short[][] combinations(short[] inputSet) {
        if (inputSet == null) {
            return (null);
        } else {
            short[][] outputSet = new short[getCombinations(inputSet)][];

            combinations(inputSet, 0, null, outputSet, 0);

            return (outputSet);
        }
    }

    /**
     * Recursively calculates all possible combinations of a given item
     * set.
     * @param inputSet the given item set.
     * @param inputIndex the index within the input set marking current
     * element under consideration (0 at start).
     * @param sofar the part of a combination determined sofar during the
     * recursion (null at start).
     * @param outputSet the combinations collected so far, will hold all
     * combinations when recursion ends.
     * @param outputIndex the current location in the output set.
     * @return revised output index. 
     */
    private int combinations(short[] inputSet, int inputIndex, short[] sofar, short[][] outputSet, int outputIndex) {
        short[] tempSet;
        int     index = inputIndex;

        // Loop through input array
        while (index < inputSet.length) {
            tempSet                = realloc1(sofar, inputSet[index]);
            outputSet[outputIndex] = tempSet;
            outputIndex            = combinations(inputSet, index + 1, copyItemSet(tempSet), outputSet,
                    outputIndex + 1);
            index++;
        }

        // Return
        return (outputIndex);
    }

    /* GET COMBINATTIONS */

    /**
     * Gets the number of possible combinations of a given item set.
     * @param set the given item set.
     * @return number of possible combinations. 
     */
    private int getCombinations(short[] set) {
        int counter = 0, numComb;

        numComb = (int) Math.pow(2.0, set.length) - 1;

        // Return
        return (numComb);
    }

    /* ---------------------------------------------------------------- */
    /*  */
    /* MISCELANEOUS */
    /*  */
    /* ---------------------------------------------------------------- */
    /* COPY ITEM SET */

    /**
     * Makes a copy of a given itemSet.
     * @param itemSet the given item set.
     * @return copy of given item set. 
     */
    protected short[] copyItemSet(short[] itemSet) {

        // Check whether there is a itemSet to copy
        if (itemSet == null) {
            return (null);
        }

        // Do copy and return
        short[] newItemSet = new short[itemSet.length];

        for (int index = 0; index < itemSet.length; index++) {
            newItemSet[index] = itemSet[index];
        }

        // Return
        return (newItemSet);
    }

    /* ------------------------------------------------- */
    /*  */
    /* OUTPUT METHODS */
    /*  */
    /* ------------------------------------------------- */
    /* ----------------- */
    /* OUTPUT DATA TABLE */
    /* ----------------- */

    /**
     * Outputs stored input data set; initially read from input data file, but
     * may be reordered or pruned if desired by a particular application. 
     */
    public void outputDataArray() {
        if (isPrunedFlag) {
            System.out.println("DATA SET (Ordered and Pruned)\n" + "-----------------------------");
        } else {
            if (isOrderedFlag) {
                System.out.println("DATA SET (Ordered)\n" + "------------------");
            } else {
                System.out.println("DATA SET\n" + "--------");
            }
        }

        // Loop through data array
        for (int index = 0; index < dataArray.length; index++) {
            outputItemSet(dataArray[index]);
            System.out.println();
        }
    }

    /**
     * Outputs the given array of array of short integers.
     * <P> Used for diagnostic purposes.
     * @param dataSet the five array of arrays. 
     */
    protected void outputDataArray(short[][] dataSet) {
        if (dataSet == null) {
            System.out.println("null");

            return;
        }

        // Loop through data array
        for (int index = 0; index < dataSet.length; index++) {
            outputItemSet(dataSet[index]);
            System.out.println();
        }
    }

    /* -------------- */
    /* OUTPUT ITEMSET */
    /* -------------- */

    /**
     * Outputs a given item set.
     * @param itemSet the given item set. 
     */
    protected void outputItemSet(short[] itemSet) {

        // Check for empty set
        if (itemSet == null) {
            System.out.print(" null ");

            // Process
        } else {

            // Reconvert where input dataset has been reordered and possible
            // pruned.
            short[] tempItemSet = reconvertItemSet(itemSet);

            // Loop through item set elements
            int counter = 0;

            for (int index = 0; index < tempItemSet.length; index++) {
                if (counter == 0) {
                    counter++;
                    System.out.print(" {");
                } else {
                    System.out.print(" ");
                }

                System.out.print(tempItemSet[index]);
            }

            System.out.print("} ");
        }
    }

    /* ---------------------- */
    /* OUTPUT DATA ARRAY SIZE */
    /* ---------------------- */

    /**
     * Outputs size (number of records and number of elements) of stored
     * input data set read from input data file. 
     */
    public void outputDataArraySize() {
        int numRecords  = 0;
        int numElements = 0;

        // Loop through data array
        for (int index = 0; index < dataArray.length; index++) {
            if (dataArray[index] != null) {
                numRecords++;
                numElements = numElements + dataArray[index].length;
            }
        }

        // Output
        System.out.println("Number of records        = " + numRecords);
        System.out.println("Number of elements       = " + numElements);

        double density = (double) numElements / (numCols * numRecords);

        System.out.println("Data set density   = " + twoDecPlaces(density) + "%");
    }

    /* ------------------------ */
    /* OUTPUT CONVERSION ARRAYS */
    /* ------------------------ */

    /**
     * Outputs conversion array (used to renumber columns for input data
     * in terms of frequency of single attributes --- reordering will enhance
     * performance for some ARM algorithms). 
     */
    public void outputConversionArrays() {

        // Conversion array
        System.out.println("Conversion Array = ");

        for (int index = 1; index < conversionArray.length; index++) {
            System.out.println("(" + index + ") " + conversionArray[index][0] + " = " + conversionArray[index][1]);
        }

        // Reconversion array
        System.out.println("Reonversion Array = ");

        for (int index = 1; index < reconversionArray.length; index++) {
            System.out.println("(" + index + ") " + reconversionArray[index]);
        }
    }

    /* ----------- */
    /* OUTPUT MENU */
    /* ----------- */

    /** Outputs menu for command line arguments. */
    protected void outputMenu() {
        System.out.println();
        System.out.println("-F  = File name");
        System.out.println("-S  = Support (default 20%)");
        System.out.println();

        // Exit
        System.exit(1);
    }

    /* --------------- */
    /* OUTPUT SETTINGS */
    /* --------------- */

    /** Outputs command line values provided by user. */
    protected void outputSettings() {
        System.out.println("SETTINGS\n--------");
        System.out.println("File name                = " + fileName);
        System.out.println("Support (default 20%)    = " + support);
        System.out.println();
    }

    /* OUTPUT SETTINGS */

    /** Outputs instance field values. */
    protected void outputSettings2() {
        System.out.println("SETTINGS\n--------");
        System.out.println("Number of records        = " + numRows);
        System.out.println("Number of columns        = " + numCols);
        System.out.println("Support (default 20%)    = " + support);
        System.out.println("Confidence (default 80%) = " + confidence);
        System.out.println("Min support              = " + minSupport + " (records)");
        System.out.println("Num one itemsets         = " + numOneItemSets);
    }

    /* -------------------------------------- */
    /* OUTPUT SUPPORT AND CONFIDENCE SETTINGS */
    /* -------------------------------------- */

    /** Outputs current support and confidence settings. */
    public void outputSuppAndConf() {
        System.out.println("Support = " + twoDecPlaces(support) + ", Confidence = " + twoDecPlaces(confidence));
    }

    /* ------------------------ */
    /* OUTPUT RULE LINKED LISTS */
    /* ------------------------ */

    /**
     * Outputs contents of rule linked list (if any) asuming that the list
     * represents a set of ARs.    
     */
    public void outputRules() {
        outputRules(startRulelist);
    }

    /**
     * Outputs given rule list.
     * @param ruleList the given rule list. 
     */
    public void outputRules(RuleNode ruleList) {

        // Check for empty rule list
        if (ruleList == null) {
            System.out.println("No rules generated!");
        }

        // Loop through rule list
        int      number       = 1;
        RuleNode linkRuleNode = ruleList;

        while (linkRuleNode != null) {
            System.out.print("(" + number + ") ");
            outputRule(linkRuleNode);
            System.out.println(" " + twoDecPlaces(linkRuleNode.confidenceForRule) + "%");
            number++;
            linkRuleNode = linkRuleNode.next;
        }
    }

    /**
     * Outputs a rule asuming that the rule represents an ARs.
     * @param rule the rule to be output. 
     */
    private void outputRule(RuleNode rule) {
        outputItemSet(rule.antecedent);
        System.out.print(" -> ");
        outputItemSet(rule.consequent);
    }

    /* OUTPUT RULE LINKED LIST WITH DEFAULT */

    /**
     * Outputs contents of rule linked list (if any), with reconversion, such
     * that last rule is the default rule. 
     */
    public void outputRulesWithDefault() {
        int      number       = 1;
        RuleNode linkRuleNode = startRulelist;

        while (linkRuleNode != null) {

            // Output rule number
            System.out.print("(" + number + ") ");

            // Output antecedent
            if (linkRuleNode.next == null) {
                System.out.print("Default -> ");
            } else {
                outputItemSet(linkRuleNode.antecedent);
                System.out.print(" -> ");
            }

            // Output concequent
            outputItemSet(linkRuleNode.consequent);
            System.out.println(" " + twoDecPlaces(linkRuleNode.confidenceForRule) + "%");

            // Increment parameters
            number++;
            linkRuleNode = linkRuleNode.next;
        }
    }

    /* --------------------------------- */
    /*  */
    /* DIAGNOSTIC OUTPUT */
    /*  */
    /* --------------------------------- */
    /* OUTPUT DURATION */

    /**
     * Outputs difference between two given times.
     * @param time1 the first time.
     * @param time2 the second time.
     * @return duration. 
     */
    public double outputDuration(double time1, double time2) {
        double duration = (time2 - time1) / 1000;

        System.out.println("Generation time = " + twoDecPlaces(duration) + " seconds (" + twoDecPlaces(duration / 60)
                           + " mins)");

        // Return
        return (duration);
    }

    /* -------------------------------- */
    /*  */
    /* OUTPUT UTILITIES */
    /*  */
    /* -------------------------------- */
    /* TWO DECIMAL PLACES */

    /**
     * Converts given real number to real number rounded up to two decimal
     * places.
     * @param number the given number.
     * @return the number to two decimal places. 
     */
    protected double twoDecPlaces(double number) {
        int numInt = (int) ((number + 0.005) * 100.0);

        number = ((double) numInt) / 100.0;

        return (number);
    }

    /* ------ FIELDS ------ */

    // Inner class for storing linked list of ARs or CARs as appropriate.
    protected class RuleNode {

        /**
         * The confidence value associate with the rule represented by this
         * node. 
         */
        double confidenceForRule = 0.0;

        /** Link to next node */
        RuleNode next = null;

        /** Antecedent of AR. */
        protected short[] antecedent;

        /** Consequent of AR. */
        protected short[] consequent;

        /**
         * Three argument constructor
         * @param ante the antecedent (LHS) of the AR.
         * @param cons the consequent (RHS) of the AR.
         * @param confValue the associated confidence value. 
         */
        protected RuleNode(short[] ante, short[] cons, double confValue) {
            antecedent        = ante;
            consequent        = cons;
            confidenceForRule = confValue;
        }
    }
}
