import class_switch as s

class ToggleSwitch(s.Switch):
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def _setAdditionalDumpables(self):
        self._dumpables += [str(self.getMainProperty()), 'false']
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def toggle(self):
        self._state = not self._state
    
    
