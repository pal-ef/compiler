import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static HashMap<String, String> dictionary = new HashMap<String, String>();

    private static void populateDictionary() {
        // Read file with language rules
        try {
            File object = new File("src/rules.miku");
            Scanner scan = new Scanner(object);

            // Read every line and add it to local dictionary
            while(scan.hasNextLine()) {
                String data = scan.nextLine();
                if (data.isBlank()) continue;
                String[] rule = data.split(":");
                dictionary.put(rule[0], rule[1]);
            }

            System.out.println("\uD83D\uDFE2 Language rules successfully loaded!");
        } catch (FileNotFoundException e) {
            System.out.println("Language rules not found.");
        }
    }

    private static boolean validateIdentifier(String s) {
        String regexPattern = "^[a-zA-Z_$][a-zA-Z_$0-9]*$";
        return Pattern.compile(regexPattern).matcher(s).matches();
    }

    private static void isSymbol(String s) {
        // Verify that current symbol is valid inside language dictionary
        if(dictionary.containsKey(s)) {
            System.out.println("✅ Recognized: " + dictionary.get(s) + "(" + s + ")");
        } else {
            // Verify if symbol is number
            try {
                int num = Integer.parseInt(s);
                System.out.println("✅ Recognized: Integer" +  "(" + num + ")");
            } catch (NumberFormatException e) {
                // verify if symbol is string
                if(s.charAt(0) == '"') {
                    System.out.println("✅ Recognized: content(" + s + ")");
                    return;
                }

                // verify if valid identifier, probably using regex
                if(validateIdentifier(s)) {
                    System.out.println("✅ Recognized: identifier(" + s + ")");
                } else System.out.println("❌ Unrecognized: " + s);
            }
        }
    }

    public static void main(String[] args) {
        // Populate language dictionary
        populateDictionary();

        // Get content for lexer
        System.out.print("Test prompt: ");
        Scanner scanner = new Scanner(System.in);
        String content = scanner.nextLine();

        // Real-time built string
        StringBuilder currentString = new StringBuilder();

        // Auxiliary variables
        int quoteCount = 0;
        int parenthesisCount = 0;

        // Read character by character
        for(int i = 0; i < content.length(); i++) {
            // Get current char
            char current = content.charAt(i);

            // Check for quotes
            if(current == '"') quoteCount++;

            // if quoteCount is not a pair skip
            if(quoteCount % 2 != 0) {
                // Add this char to the currentString
                currentString.append(current);
                // Skip
                continue;
            }

            // Watch for spaces while not inside parenthesis
            if(Character.isWhitespace(current) || current == ';') {
                // Check for symbol
                isSymbol(currentString.toString());

                // Empty the currentString
                currentString = new StringBuilder();

                // if semicolon
                if(current == ';') {
                    System.out.println("\uD83D\uDE48 Semicolon");
                }

                // skip adding this space
                continue;
            }

            // Add this char to the currentString
            currentString.append(current);

        }

        // Lastly, there can be another symbol at the end.
        // Verify that the currentString is not empty
        // if not then verify if it's a symbol
        if(!currentString.toString().isBlank()) {
            isSymbol(currentString.toString());
        }
    }
}