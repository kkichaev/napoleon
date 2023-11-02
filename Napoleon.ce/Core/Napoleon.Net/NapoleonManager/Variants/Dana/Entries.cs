/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
using System;
using System.Collections.Generic;
namespace GRSoft.NapoleonManager
{
    class FormEntries
    {
        public static readonly string DISABLE_SAVE = "LimitEditRoute";


        internal static DivisionForm OpenDivisionForm()
        {
            return new DivisionForm();
        }

        internal static FmDetail OpenDetailForm(FmDetailData data)
        {
            return new FmDetail(data);
        }

        internal static UserForm OpenUserForm(Divisions owner)
        {
            return new UserFormEx(owner);
        }

        internal static FmCensus OpenCensusForm()
        {
            return new FmCensus();
        }

        internal static System.Type GetFormType(System.Type baseType)
        {

            //if (baseType == typeof(FmPrice))
            //   return typeof(FmPricePhoto);

            //if (baseType == typeof(MainForm))
            //   return typeof(MainFormEx);

            if (baseType == typeof(Divisions))
                return typeof(DivisionsEx);
            if (baseType == typeof(FmScriptEdit))
                return typeof(FmScriptEditEx);
            if (baseType == typeof(FmMatrixDesigner))
                return typeof(FmMatrixDesignerEx);
            if (baseType == typeof(FmReports))
                return typeof(FmReportsEx);

            return baseType;
        }
    }
}