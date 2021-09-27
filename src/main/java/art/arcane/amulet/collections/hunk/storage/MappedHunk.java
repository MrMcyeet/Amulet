/*
 * Amulet is an extension api for Java
 * Copyright (c) 2021 Arcane Arts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package art.arcane.amulet.collections.hunk.storage;

import art.arcane.amulet.collections.hunk.Hunk;
import art.arcane.amulet.functional.Consume;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"DefaultAnnotationParam", "Lombok"})
@Data
@EqualsAndHashCode(callSuper = false)
public class MappedHunk<T> extends StorageHunk<T> implements Hunk<T> {
    private final Map<Integer, T> data;

    public MappedHunk(int w, int h, int d) {
        super(w, h, d);
        data = new ConcurrentHashMap<>();
    }

    public int getEntryCount() {
        return data.size();
    }

    public boolean isMapped() {
        return true;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public void setRaw(int x, int y, int z, T t) {
        if (t == null) {
            data.remove(index(x, y, z));
            return;
        }

        data.put(index(x, y, z), t);
    }

    private Integer index(int x, int y, int z) {
        return (z * getWidth() * getHeight()) + (y * getWidth()) + x;
    }

    @Override
    public synchronized Hunk<T> iterateSync(Consume.Four<Integer, Integer, Integer, T> c) {
        int idx, z;

        for (Map.Entry<Integer, T> g : data.entrySet()) {
            idx = g.getKey();
            z = idx / (getWidth() * getHeight());
            idx -= (z * getWidth() * getHeight());
            c.accept(idx % getWidth(), idx / getWidth(), z, g.getValue());
        }

        return this;
    }

    @Override
    public synchronized Hunk<T> iterateSyncIO(Consume.FourIO<Integer, Integer, Integer, T> c) throws IOException {
        int idx, z;

        for (Map.Entry<Integer, T> g : data.entrySet()) {
            idx = g.getKey();
            z = idx / (getWidth() * getHeight());
            idx -= (z * getWidth() * getHeight());
            c.accept(idx % getWidth(), idx / getWidth(), z, g.getValue());
        }

        return this;
    }

    @Override
    public void empty(T b) {
        data.clear();
    }

    @Override
    public T getRaw(int x, int y, int z) {
        return data.get(index(x, y, z));
    }
}
