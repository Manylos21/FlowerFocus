package com.example.flowerfocus.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.example.flowerfocus.model.Flower;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class FlowerDao_Impl implements FlowerDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Flower> __insertAdapterOfFlower;

  public FlowerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFlower = new EntityInsertAdapter<Flower>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `flowers` (`id`,`sessionId`,`type`,`level`,`progression`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Flower entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.sessionId);
        if (entity.type == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.type);
        }
        statement.bindLong(4, entity.level);
        statement.bindDouble(5, entity.progression);
      }
    };
  }

  @Override
  public long insertFlower(final Flower flower) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFlower.insertAndReturnId(_connection, flower);
    });
  }

  @Override
  public Flower getFlowerBySessionId(final int sessionId) {
    final String _sql = "SELECT * FROM flowers WHERE sessionId = ? LIMIT 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, sessionId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSessionId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sessionId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLevel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "level");
        final int _columnIndexOfProgression = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "progression");
        final Flower _result;
        if (_stmt.step()) {
          _result = new Flower();
          _result.id = (int) (_stmt.getLong(_columnIndexOfId));
          _result.sessionId = (int) (_stmt.getLong(_columnIndexOfSessionId));
          if (_stmt.isNull(_columnIndexOfType)) {
            _result.type = null;
          } else {
            _result.type = _stmt.getText(_columnIndexOfType);
          }
          _result.level = (int) (_stmt.getLong(_columnIndexOfLevel));
          _result.progression = (float) (_stmt.getDouble(_columnIndexOfProgression));
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
  public int getTotalFlowers() {
    final String _sql = "SELECT COUNT(*) FROM flowers";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _result;
        if (_stmt.step()) {
          _result = (int) (_stmt.getLong(0));
        } else {
          _result = 0;
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
}
