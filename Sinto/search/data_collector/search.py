import tkinter as tk
from tkinter import ttk
from sqlite3.dbapi2 import Connection, Cursor

from data_collector import Tokens

class App(tk.Frame):
    def clicked(self):
        text = self.input.get()
        tokens, qtokens = self.tokens.findTokens(text)
        print(text, tokens)

    def __init__(self, db:Connection, master = None):
        super().__init__(master, padx=10, pady=10)

        self.tokens = Tokens(db)
        self.grid()

        ttk.Label(self, text="Строка поиска").grid(column=0, row=0, sticky="w")
        self.input = ttk.Entry(self, width=100)
        self.input.grid(column=0, row=1)
        self.button = ttk.Button(self, text="Поиск", command=self.clicked)
        self.button.grid(column=1, row=1, padx=20)



def open(db:Connection):
    root = App(db)

    root.mainloop()

