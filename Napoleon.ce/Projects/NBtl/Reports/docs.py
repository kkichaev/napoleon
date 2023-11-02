from manager.document import BaseDocument, docTypes, DocType

def InitDocuments():
    for dt in docTypes:
        if dt.objectName == "CMonitoring":
            return

    docTypes.append(DocType("CMonitoring", "Мониторинг", BaseDocument))
    docTypes.append(DocType("Distrib", "Дистрибуция", BaseDocument))
    docTypes.append(DocType("Planogram", "Планограмма", BaseDocument))
    docTypes.append(DocType("Contract", "Доля полки", BaseDocument))
    docTypes.append(DocType("TaskDone", "Задачи", BaseDocument))
