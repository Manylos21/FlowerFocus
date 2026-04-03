package com.example.flowerfocus.dao;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteConnection;
import androidx.sqlite.SQLiteStatement;
import com.example.flowerfocus.model.Flower;
import com.example.flowerfocus.model.Session;
import com.example.flowerfocus.model.SessionWithFlower;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class SessionDao_Impl implements SessionDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Session> __insertAdapterOfSession;

  public SessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfSession = new EntityInsertAdapter<Session>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sessions` (`id`,`plannedDuration`,`realDuration`,`date`,`status`,`category`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Session entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.plannedDuration);
        statement.bindLong(3, entity.realDuration);
        statement.bindLong(4, entity.date);
        if (entity.status == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.status);
        }
        if (entity.category == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.category);
        }
      }
    };
  }

  @Override
  public long insertSession(final Session session) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfSession.insertAndReturnId(_connection, session);
    });
  }

  @Override
  public LiveData<List<Session>> getAllSessions() {
    final String _sql = "SELECT * FROM sessions ORDER BY date DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"sessions"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfPlannedDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "plannedDuration");
        final int _columnIndexOfRealDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "realDuration");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final List<Session> _result = new ArrayList<Session>();
        while (_stmt.step()) {
          final Session _item;
          _item = new Session();
          _item.id = (int) (_stmt.getLong(_columnIndexOfId));
          _item.plannedDuration = _stmt.getLong(_columnIndexOfPlannedDuration);
          _item.realDuration = _stmt.getLong(_columnIndexOfRealDuration);
          _item.date = _stmt.getLong(_columnIndexOfDate);
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _item.status = null;
          } else {
            _item.status = _stmt.getText(_columnIndexOfStatus);
          }
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _item.category = null;
          } else {
            _item.category = _stmt.getText(_columnIndexOfCategory);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<SessionWithFlower>> getAllSessionsWithFlowers() {
    final String _sql = "SELECT * FROM sessions ORDER BY date DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"flowers",
        "sessions"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfPlannedDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "plannedDuration");
        final int _columnIndexOfRealDuration = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "realDuration");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final LongSparseArray<Flower> _collectionFlower = new LongSparseArray<Flower>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfId);
          }
          if (_tmpKey != null) {
            _collectionFlower.put(_tmpKey, null);
          }
        }
        _stmt.reset();
        __fetchRelationshipflowersAscomExampleFlowerfocusModelFlower(_connection, _collectionFlower);
        final List<SessionWithFlower> _result = new ArrayList<SessionWithFlower>();
        while (_stmt.step()) {
          final SessionWithFlower _item;
          final Session _tmpSession;
          if (!(_stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfPlannedDuration) && _stmt.isNull(_columnIndexOfRealDuration) && _stmt.isNull(_columnIndexOfDate) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfCategory))) {
            _tmpSession = new Session();
            _tmpSession.id = (int) (_stmt.getLong(_columnIndexOfId));
            _tmpSession.plannedDuration = _stmt.getLong(_columnIndexOfPlannedDuration);
            _tmpSession.realDuration = _stmt.getLong(_columnIndexOfRealDuration);
            _tmpSession.date = _stmt.getLong(_columnIndexOfDate);
            if (_stmt.isNull(_columnIndexOfStatus)) {
              _tmpSession.status = null;
            } else {
              _tmpSession.status = _stmt.getText(_columnIndexOfStatus);
            }
            if (_stmt.isNull(_columnIndexOfCategory)) {
              _tmpSession.category = null;
            } else {
              _tmpSession.category = _stmt.getText(_columnIndexOfCategory);
            }
          } else {
            _tmpSession = null;
          }
          final Flower _tmpFlower;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfId);
          }
          if (_tmpKey_1 != null) {
            _tmpFlower = _collectionFlower.get(_tmpKey_1);
          } else {
            _tmpFlower = null;
          }
          _item = new SessionWithFlower();
          _item.session = _tmpSession;
          _item.flower = _tmpFlower;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Long> getTotalFocusTime() {
    final String _sql = "SELECT SUM(realDuration) FROM sessions WHERE status = 'completed'";
    return __db.getInvalidationTracker().createLiveData(new String[] {"sessions"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Long _result;
        if (_stmt.step()) {
          final Long _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(0);
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Integer> getCompletedCount() {
    final String _sql = "SELECT COUNT(*) FROM sessions WHERE status = 'completed'";
    return __db.getInvalidationTracker().createLiveData(new String[] {"sessions"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<String>> getDistinctDays() {
    final String _sql = "SELECT DISTINCT date(date/1000, 'unixepoch') FROM sessions WHERE status = 'completed' ORDER BY date DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"sessions"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final List<String> _result = new ArrayList<String>();
        while (_stmt.step()) {
          final String _item;
          final String _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(0);
          }
          _item = _tmp;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipflowersAscomExampleFlowerfocusModelFlower(
      @NonNull final SQLiteConnection _connection, @NonNull final LongSparseArray<Flower> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (_tmpMap) -> {
        __fetchRelationshipflowersAscomExampleFlowerfocusModelFlower(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `id`,`sessionId`,`type`,`level`,`progression` FROM `flowers` WHERE `sessionId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final SQLiteStatement _stmt = _connection.prepare(_sql);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    try {
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "sessionId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfId = 0;
      final int _columnIndexOfSessionId = 1;
      final int _columnIndexOfType = 2;
      final int _columnIndexOfLevel = 3;
      final int _columnIndexOfProgression = 4;
      while (_stmt.step()) {
        final long _tmpKey;
        _tmpKey = _stmt.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final Flower _item_1;
          _item_1 = new Flower();
          _item_1.id = (int) (_stmt.getLong(_columnIndexOfId));
          _item_1.sessionId = (int) (_stmt.getLong(_columnIndexOfSessionId));
          if (_stmt.isNull(_columnIndexOfType)) {
            _item_1.type = null;
          } else {
            _item_1.type = _stmt.getText(_columnIndexOfType);
          }
          _item_1.level = (int) (_stmt.getLong(_columnIndexOfLevel));
          _item_1.progression = (float) (_stmt.getDouble(_columnIndexOfProgression));
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _stmt.close();
    }
  }
}
