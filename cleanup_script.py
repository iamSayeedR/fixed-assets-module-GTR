import os
import re
import glob

# Configuration
DELETED_FILES_MD = "DELETED_FILES.md"
ROOT_DIR = os.getcwd()

# Patterns to matching in MD file
# Identifying lines like "- src\..." or "- db\..."
path_pattern = re.compile(r'^\s*-\s+(src[\\/].*|db[\\/].*)')

keep_files = set()
keep_globs = set()

print("Reading keep list from " + DELETED_FILES_MD)
try:
    with open(DELETED_FILES_MD, 'r', encoding='utf-8') as f:
        for line in f:
            match = path_pattern.match(line)
            if match:
                path_str = match.group(1).strip()
                # Handle regex/globs in the Keep List (e.g. 43-*)
                if '*' in path_str:
                    # Normalize glob path
                    normalized_glob = os.path.normpath(os.path.join(ROOT_DIR, path_str))
                    keep_globs.add(normalized_glob)
                else:
                    # Specific file
                    normalized_path = os.path.normpath(os.path.join(ROOT_DIR, path_str))
                    keep_files.add(normalized_path.lower()) # Case insensitive check for Windows
except FileNotFoundError:
    print(f"Error: {DELETED_FILES_MD} not found!")
    exit(1)

print(f"Found {len(keep_files)} specific files and {len(keep_globs)} glob patterns to keep.")

# Helper to check if a file should be kept
def should_keep(file_path):
    abs_path = os.path.normpath(file_path)
    if abs_path.lower() in keep_files:
        return True
    
    # Check globs
    # We can't easily check 'does file match glob', but we can expand globs and check membership?
    # Or just simple prefix check since the globs are simple '43-*'
    # Actually, expanding globs once is better if they refer to existing files.
    # But if the glob is generic, I should check pattern.
    # The globs are like `.../43-*`.
    for g in keep_globs:
        # Simple wildcard handling:
        # Convert glob to regex?
        # Windows paths might have backslashes, escape them.
        import fnmatch
        if fnmatch.fnmatch(abs_path, g):
            return True
    return False

# Directories to clean
scan_dirs = [
    os.path.join(ROOT_DIR, "src", "main", "java"),
    os.path.join(ROOT_DIR, "db", "changelog", "changes")
]

deleted_count = 0

for scan_dir in scan_dirs:
    if not os.path.exists(scan_dir):
        continue
    
    for root, dirs, files in os.walk(scan_dir):
        for name in files:
            file_path = os.path.join(root, name)
            # Filter only relevant extensions if needed? User said "codes and liquibase".
            # Java files in src
            if "src\\main\\java" in scan_dir and not name.endswith(".java"):
                continue # Skip non-java files in src/main/java? Or strict cleanup?
                # User said "only the codes". I'll stick to .java for safety unless instructed otherwise.
            
            # XML/Scanning in db
            if "db\\changelog" in scan_dir:
                pass # check all files in db/changelog/changes
            
            if should_keep(file_path):
                # print(f"Keeping: {file_path}")
                pass
            else:
                try:
                    print(f"Deleting: {file_path}")
                    os.remove(file_path)
                    deleted_count += 1
                except Exception as e:
                    print(f"Failed to delete {file_path}: {e}")

print(f"Cleanup complete. Deleted {deleted_count} files.")
