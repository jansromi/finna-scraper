import subprocess

input_file = ""

# Specify your .jar file path
jar_file = "C:/koodi/java/finnahaku/finnahaku.jar"

# Command line args
command = ["java", "-jar", jar_file, "9789520102814", "-s"]

# Open the output file
with open('output.txt', 'a') as f:
    # Start the .jar file and redirect its output to the file
    process = subprocess.Popen(command, stdout=f)
    
# Wait for the process to finish
process.wait()