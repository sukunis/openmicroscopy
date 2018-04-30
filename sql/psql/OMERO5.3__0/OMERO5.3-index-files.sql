-- Copyright (C) 2016 Glencoe Software, Inc. All rights reserved.
-- Use is subject to license terms supplied in LICENSE.txt
--
-- This program is free software; you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation; either version 2 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License along
-- with this program; if not, write to the Free Software Foundation, Inc.,
-- 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
--

---
--- OMERO5 in-place upgrade for the OMERO5.3__0 database for 2017-SV5.
---

BEGIN;


--
-- check OMERO database version
--

CREATE OR REPLACE FUNCTION omero_assert_db_version(expected_version VARCHAR, expected_patch INTEGER)
    RETURNS void AS $$

DECLARE
    current_version VARCHAR;
    current_patch INTEGER;

BEGIN
    SELECT currentversion, currentpatch INTO STRICT current_version, current_patch
        FROM dbpatch ORDER BY id DESC LIMIT 1;

    IF current_version <> expected_version OR current_patch <> expected_patch THEN
        RAISE EXCEPTION 'wrong OMERO database version for this upgrade script';
    END IF;

END;$$ LANGUAGE plpgsql;

SELECT omero_assert_db_version('OMERO5.3', 0);
DROP FUNCTION omero_assert_db_version(varchar, int);


--
-- Actual upgrade
--

INSERT INTO dbpatch (currentVersion, currentPatch, previousVersion, previousPatch)
             VALUES ('OMERO5.3',     0,            'OMERO5.3',      0);

DROP INDEX originalfile_repo_path_index;

CREATE UNIQUE INDEX originalfile_repo_path_index ON originalfile
    (repo, regexp_split_to_array('/' || path || name || '/', '/+'))
    WHERE repo IS NOT NULL;


--
-- FINISHED
--

UPDATE dbpatch SET message = 'File index updated.', finished = clock_timestamp()
    WHERE id IN (SELECT id FROM dbpatch ORDER BY id DESC LIMIT 1);

SELECT 'YOU HAVE SUCCESSFULLY PATCHED FOR 2017-SV5' AS Status;

COMMIT;
