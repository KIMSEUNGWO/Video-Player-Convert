package com.video.jours.component;

import com.video.jours.enums.PathType;

import java.nio.file.Path;

public interface PathManager {

    Path get(PathType type, String key);
}
