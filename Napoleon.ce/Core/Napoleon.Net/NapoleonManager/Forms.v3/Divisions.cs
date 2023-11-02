/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма подразделений
 * 
 * ert   03/05/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Globalization;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class Divisions : Form
   {
      public Divisions()
      {
         InitializeComponent();
         __Initing();
      }
   }
}
