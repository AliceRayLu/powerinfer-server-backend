import yaml
import sys

def write_yaml(model_path: str, output_path: str, file_path: str, isTrain: bool) -> int:
    try:
        with open(file_path, "r+") as f:
            data = yaml.safe_load(f) or {}
            
            data["model_name_or_path"] = model_path
            if not isTrain:
                data["export_dir"] = output_path
                
            f.seek(0)
            yaml.safe_dump(data, f)
            f.truncate()
        return 0
    except Exception as e:
        print(f"An error occurred: {e}")
        return -1
        
if __name__ == "__main__":
    code = write_yaml(sys.argv[1], sys.argv[2], sys.argv[3], True)
    code += write_yaml(sys.argv[1], sys.argv[2], sys.argv[4], False)
    
    if code == 0:
        print("Success")
    else:
        print("Fail to write yaml script.")
        sys.exit(1)