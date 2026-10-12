/*
 * Name:       Garvin Stewart
 * Date:       10/11/2026
 * Assignment: CSD 420 Module 11 - Java JSON API (supplemental Jackson example)
 *
 * Purpose: Demonstrate Jackson's Tree Model and Streaming API by reading,
 *          modifying, writing, and re-reading JSON.
 *
 * Source / citation:
 *   Adapted from the "3 minute tutorial: Tree Model" and "5 minute tutorial:
 *   Streaming parser, generator" sections of the jackson-databind README,
 *   FasterXML (2026), https://github.com/FasterXML/jackson-databind
 *   Changes from the original: wrapped in a runnable class, swapped the sample
 *   data for a course-themed record, replaced the deprecated getCurrentName()
 *   with currentName(), used try-with-resources for the generator, and added
 *   a field-by-field token loop and printed output for each step.
 *
 * Library:  Jackson 2.x (com.fasterxml.jackson), tested with 2.22.0 on JDK 21.
 *           Requires jackson-core, jackson-annotations, and jackson-databind
 *           on the classpath (see compile/run commands at the bottom).
 */

public class JacksonTreeAndStreamingExample {

  public static void main(String[] args) throws java.io.IOException {

    // One ObjectMapper is created and reused; it is thread-safe once configured
    com.fasterxml.jackson.databind.ObjectMapper mapper =
        new com.fasterxml.jackson.databind.ObjectMapper();

    // ---------- Part 1: Tree Model (JsonNode) ----------

    // Parse a JSON string into an in-memory tree without defining a Java class
    String courseJson = "{ \"course\": \"CSD 420\", \"credits\": 3, "
        + "\"topics\": [\"Binary I/O\", \"Generics\", \"JDBC\", \"JSON\"] }";
    com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(courseJson);

    // Read individual values by field name
    String courseName = rootNode.get("course").asText();
    int creditHours = rootNode.get("credits").asInt();
    System.out.println("Course: " + courseName + " (" + creditHours + " credits)");

    // Walk an array node like any other collection
    com.fasterxml.jackson.databind.JsonNode topicArray = rootNode.get("topics");
    System.out.println("Topic count: " + topicArray.size());
    for (com.fasterxml.jackson.databind.JsonNode topicNode : topicArray) {
      System.out.println("  - " + topicNode.asText());
    }

    // Modify the tree: withObject() creates the nested "instructor" object
    // if it does not exist yet, then put() adds a field to it
    ((com.fasterxml.jackson.databind.node.ObjectNode) rootNode)
        .withObject("/instructor").put("office", "online");

    // Serialize the modified tree back to pretty-printed JSON
    String updatedJson = mapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(rootNode);
    System.out.println("\nModified tree as JSON:\n" + updatedJson);

    // ---------- Part 2: Streaming API (JsonGenerator / JsonParser) ----------

    // Write JSON token by token to a file; nothing is held in memory as a tree
    java.io.File jsonFile = new java.io.File("message.json");
    try (com.fasterxml.jackson.core.JsonGenerator generator =
             mapper.createGenerator(jsonFile,
                 com.fasterxml.jackson.core.JsonEncoding.UTF8)) {
      generator.writeStartObject();
      generator.writeStringField("message", "Hello from the streaming API!");
      generator.writeNumberField("module", 11);
      generator.writeEndObject();
    }
    System.out.println("\nWrote " + jsonFile.getName()
        + " (" + jsonFile.length() + " bytes)");

    // Read the same file back one token at a time
    try (com.fasterxml.jackson.core.JsonParser parser =
             mapper.createParser(jsonFile)) {

      // The first token must open an object
      if (parser.nextToken() != com.fasterxml.jackson.core.JsonToken.START_OBJECT) {
        throw new java.io.IOException("Expected the file to start with '{'");
      }

      // Loop over field-name/value pairs until the closing brace
      while (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME) {
        String fieldName = parser.currentName();
        com.fasterxml.jackson.core.JsonToken valueToken = parser.nextToken();
        System.out.println("Token " + valueToken + " -> "
            + fieldName + " = " + parser.getText());
      }
    }

    // Remove the demo file so repeated runs start clean
    jsonFile.delete();
  }
}

/*
 * Compile and run from the module-11 folder, with the three Jackson JARs
 * in the jackson-jars subfolder (unzip jackson-2.22-jars.zip into it).
 * macOS/Linux:
 *   javac -cp "jackson-jars/*" JacksonTreeAndStreamingExample.java
 *   java  -cp ".:jackson-jars/*" JacksonTreeAndStreamingExample
 * Windows: use ';' instead of ':' in the run command.
 *
 * Expected output (verified on JDK 21 and on macOS with Jackson 2.22.0):
 *   Course: CSD 420 (3 credits)
 *   Topic count: 4
 *     - Binary I/O
 *     - Generics
 *     - JDBC
 *     - JSON
 *
 *   Modified tree as JSON:
 *   {
 *     "course" : "CSD 420",
 *     "credits" : 3,
 *     "topics" : [ "Binary I/O", "Generics", "JDBC", "JSON" ],
 *     "instructor" : {
 *       "office" : "online"
 *     }
 *   }
 *
 *   Wrote message.json (55 bytes)
 *   Token VALUE_STRING -> message = Hello from the streaming API!
 *   Token VALUE_NUMBER_INT -> module = 11
 */
