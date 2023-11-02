/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Проверка последней синхронизации
 *
 *  ert   13/08/2007   creating
 */
#ifndef __DO_SYNC_H
#define __DO_SYNC_H

#define SYNC_STAMP L"NapoleonSync.dat"

void MarkSynced();
void ClearSyncFile();
bool CheckSync();


#endif
