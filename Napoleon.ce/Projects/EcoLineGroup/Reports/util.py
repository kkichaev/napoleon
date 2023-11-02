def escapeQuotes(src):
    result = ""
     
    for c in src:
        if c == '"':
            result += "\\"
             
        result += c
       
    return result