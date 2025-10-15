import re
import sys
import xml.etree.ElementTree as ET

# Paths to files
gradle_file = "app/build.gradle"
strings_file = "app/src/main/res/values/strings.xml"

# --- Read build.gradle ---
with open(gradle_file, "r") as f:
    gradle_content = f.read()

# Extract current versionCode
version_code_match = re.search(r'versionCode\s+(\d+)', gradle_content)
if not version_code_match:
    print("Error: versionCode not found in build.gradle")
    sys.exit(1)
current_version_code = int(version_code_match.group(1))

# Extract current versionName
version_name_match = re.search(r'versionName\s+\"([^\"]+)\"', gradle_content)
if not version_name_match:
    print("Error: versionName not found in build.gradle")
    sys.exit(1)
current_version_name = version_name_match.group(1)

# Parse current version into components
try:
    major, minor, patch = map(int, current_version_name.split('.'))
except ValueError:
    print(f"Error: Current versionName '{current_version_name}' is not in format X.Y.Z")
    sys.exit(1)

# --- Determine new versionName ---
new_version_name = None
if len(sys.argv) >= 2:
    arg = sys.argv[1].lower()
    if arg == "--major":
        major += 1
        minor = 0
        patch = 0
        new_version_name = f"{major}.{minor}.{patch}"
        print(f"Auto-incrementing MAJOR: {current_version_name} → {new_version_name}")
    elif arg == "--minor":
        minor += 1
        patch = 0
        new_version_name = f"{major}.{minor}.{patch}"
        print(f"Auto-incrementing MINOR: {current_version_name} → {new_version_name}")
    elif arg == "--patch":
        patch += 1
        new_version_name = f"{major}.{minor}.{patch}"
        print(f"Auto-incrementing PATCH: {current_version_name} → {new_version_name}")
    else:
        # User provided explicit version name
        new_version_name = arg
        if not re.match(r'^\d+\.\d+\.\d+$', new_version_name):
            print("Error: Version name must be in the format X.Y.Z (e.g., 1.3.0)")
            sys.exit(1)
else:
    # Default: auto-increment patch
    patch += 1
    new_version_name = f"{major}.{minor}.{patch}"
    print(f"No argument provided. Defaulting to PATCH increment: {current_version_name} → {new_version_name}")

# Increment versionCode
new_version_code = current_version_code + 1

# Replace in build.gradle
gradle_content = re.sub(r'versionCode\s+\d+', f"versionCode {new_version_code}", gradle_content)
gradle_content = re.sub(r'versionName\s+\"[^\"]+\"', f'versionName "{new_version_name}"', gradle_content)

with open(gradle_file, "w") as f:
    f.write(gradle_content)

print(f"✅ build.gradle updated: versionCode {current_version_code} → {new_version_code}, versionName → {new_version_name}")

# Update strings.xml
tree = ET.parse(strings_file)
root = tree.getroot()
for string_element in root.findall("string"):
    if string_element.attrib.get("name") == "app_version":
        string_element.text = new_version_name
tree.write(strings_file, encoding="utf-8", xml_declaration=True)

print(f"✅ strings.xml updated: app_version → {new_version_name}")

## Usage
# python update_version.py       # → 1.3.5
# python update_version.py --patch  # → 1.3.5
# python update_version.py --minor  # → 1.4.0
# python update_version.py --major  # → 2.0.0
# python update_version.py 2.1.0    # → 2.1.0
