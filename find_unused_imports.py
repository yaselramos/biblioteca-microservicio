import os
import re

def find_unused_imports(directories):
    unused_imports = []
    for directory in directories:
        for root, dirs, files in os.walk(directory):
            for file in files:
                if file.endswith(".java"):
                    file_path = os.path.join(root, file)
                    with open(file_path, 'r', encoding='utf-8') as f:
                        lines = f.readlines()
                    
                    content = "".join(lines)
                    for i, line in enumerate(lines):
                        match = re.match(r'^import (?:static )?([\w\.]+);', line)
                        if match:
                            full_import = match.group(1)
                            if full_import.endswith(".*"):
                                continue
                            
                            name = full_import.split(".")[-1]
                            # Check if 'name' is used in the content, excluding the import lines
                            # We search for the name as a whole word
                            # We need to be careful with occurrences in comments or strings, 
                            # but for a first pass this is okay.
                            # Also, we should exclude the import statements themselves when counting.
                            
                            # Remove all import lines to search in the rest of the code
                            code_without_imports = re.sub(r'^import .*;', '', content, flags=re.MULTILINE)
                            
                            # Search for the name as a whole word
                            if not re.search(r'\b' + re.escape(name) + r'\b', code_without_imports):
                                unused_imports.append((file_path, line.strip()))
    return unused_imports

if __name__ == "__main__":
    dirs = ["auth-service", "common-library", "libro-service", "prestamo-service"]
    unused = find_unused_imports(dirs)
    for file_path, imp in unused:
        print(f"{file_path}: {imp}")
