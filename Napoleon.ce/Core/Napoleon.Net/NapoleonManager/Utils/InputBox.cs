using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager.Utils
{
   class InputBox
   {
      public static DialogResult Show(string title, ref string input, IWin32Window owner = null)
      {
         System.Drawing.Size size = new System.Drawing.Size(400, 70);
         Form inputBox = new Form();

         inputBox.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
         inputBox.ClientSize = size;
         inputBox.Text = title;
         inputBox.StartPosition = FormStartPosition.CenterParent;

         System.Windows.Forms.TextBox textBox = new TextBox();
         textBox.Size = new System.Drawing.Size(size.Width - 10, 23);
         textBox.Location = new System.Drawing.Point(5, 5);
         textBox.Text = input;
         inputBox.Controls.Add(textBox);

         Button okButton = new Button();
         okButton.DialogResult = System.Windows.Forms.DialogResult.OK;
         okButton.Name = "okButton";
         okButton.Size = new System.Drawing.Size(75, 23);
         okButton.Text = "&OK";
         okButton.Location = new System.Drawing.Point(size.Width - 80 - 80, 39);
         inputBox.Controls.Add(okButton);

         Button cancelButton = new Button();
         cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         cancelButton.Name = "cancelButton";
         cancelButton.Size = new System.Drawing.Size(75, 23);
         cancelButton.Text = "&Отмена";
         cancelButton.Location = new System.Drawing.Point(size.Width - 80, 39);
         inputBox.Controls.Add(cancelButton);

         inputBox.AcceptButton = okButton;
         inputBox.CancelButton = cancelButton;

         DialogResult result = inputBox.ShowDialog(owner);
         input = textBox.Text;
         return result;
      }
   }
}
