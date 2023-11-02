class StObj:
    _set_ = None
     
    def __init__(self):
        self._set_ = list()
        
    def check(self, id):
        result = not id in self._set_
         
        if result:
            self._set_.append(id)
            
        return result    
    
        