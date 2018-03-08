package com.filelug.android.util;

import com.filelug.android.ui.model.LocalFile;
import com.filelug.android.ui.model.RemoteFile;

import java.io.File;
import java.util.Comparator;

public class SortUtils {

	private static final String TAG = MiscUtils.class.getSimpleName();

	public static class LocalFileNameAscComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				result = file1.getName().compareToIgnoreCase(file2.getName());
			}
			return result;
		}
	};

	public static class LocalFileNameDescComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				result = file2.getName().compareToIgnoreCase(file1.getName());
			}
			return result;
		}
	};

	public static class LocalFileDateAscComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				long fileTime1 = file1.getLastModifiedDate() != null ? file1.getLastModifiedDate().getTime() : -1;
				long fileTime2 = file2.getLastModifiedDate() != null ? file2.getLastModifiedDate().getTime() : -1;
				long tmp = fileTime1 - fileTime2;
				if ( tmp == 0 ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else if ( tmp > 0 ) {
					result = 1;
				} else if ( tmp < 0 ) {
					result = -1;
				}
			}
			return result;
		}
	};

	public static class LocalFileDateDescComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				long fileTime1 = file1.getLastModifiedDate() != null ? file1.getLastModifiedDate().getTime() : -1;
				long fileTime2 = file2.getLastModifiedDate() != null ? file2.getLastModifiedDate().getTime() : -1;
				long tmp = fileTime2 - fileTime1;
				if ( tmp == 0 ) {
					result = file2.getName().compareToIgnoreCase(file1.getName());
				} else if ( tmp > 0 ) {
					result = 1;
				} else if ( tmp < 0 ) {
					result = -1;
				}
			}
			return result;
		}
	};

	public static class LocalFileSizeAscComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= LocalFile.FileType.MEDIA_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					long fileSize1 = file1.getSize();
					long fileSize2 = file2.getSize();
					long tmp = fileSize1 - fileSize2;
					if ( tmp == 0 ) {
						result = file1.getName().compareToIgnoreCase(file2.getName());
					} else if ( tmp > 0 ) {
						result = 1;
					} else if ( tmp < 0 ) {
						result = -1;
					}
				}
			}
			return result;
		}
	};

	public static class LocalFileSizeDescComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= LocalFile.FileType.MEDIA_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					long fileSize1 = file1.getSize();
					long fileSize2 = file2.getSize();
					long tmp = fileSize2 - fileSize1;
					if ( tmp == 0 ) {
						result = file2.getName().compareToIgnoreCase(file1.getName());
					} else if ( tmp > 0 ) {
						result = 1;
					} else if ( tmp < 0 ) {
						result = -1;
					}
				}
			}
			return result;
		}
	};

	public static class LocalFileTypeAscComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= LocalFile.FileType.MEDIA_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					String extension1 = file1.getExtension();
					String extension2 = file2.getExtension();
					if ( extension1 == null && extension2 == null ) {
						result = 0;
					} else {
						result = extension1.compareToIgnoreCase(extension2);
					}
					if ( result == 0 ) {
						result = file1.getName().compareToIgnoreCase(file2.getName());
					}
				}
			}
			return result;
		}
	};

	public static class LocalFileTypeDescComparator implements Comparator<LocalFile> {
		@Override
		public int compare(LocalFile file1, LocalFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= LocalFile.FileType.MEDIA_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					String extension1 = file1.getExtension();
					String extension2 = file2.getExtension();
					if ( extension1 == null && extension2 == null ) {
						result = 0;
					} else {
						result = extension2.compareToIgnoreCase(extension1);
					}
					if ( result == 0 ) {
						result = file2.getName().compareToIgnoreCase(file1.getName());
					}
				}
			}
			return result;
		}
	};

	public static class FolderComparator implements Comparator<File> {
		@Override
		public int compare(File folder1, File folder2) {
			return folder1.getName().compareToIgnoreCase(folder2.getName());
		}
	}

	public static class RemoteFileNameAscComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				result = file1.getName().compareToIgnoreCase(file2.getName());
			}
			return result;
		}
	};

	public static class RemoteFileNameDescComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				result = file2.getName().compareToIgnoreCase(file1.getName());
			}
			return result;
		}
	};

	public static class RemoteFileDateAscComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				String lastModified1 = file1.getLastModified();
				String lastModified2 = file2.getLastModified();
				if ( lastModified1 == null && lastModified2 == null ) {
					result = 0;
				} else {
					result = lastModified1.compareToIgnoreCase(lastModified2);
				}
			}
			return result;
		}
	};

	public static class RemoteFileDateDescComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				String lastModified1 = file1.getLastModified();
				String lastModified2 = file2.getLastModified();
				if ( lastModified1 == null && lastModified2 == null ) {
					result = 0;
				} else {
					result = lastModified2.compareToIgnoreCase(lastModified1);
				}
			}
			return result;
		}
	};

	public static class RemoteFileSizeAscComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= RemoteFile.FileType.REMOTE_MAC_ALIAS_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					long fileSize1 = file1.getSize();
					long fileSize2 = file2.getSize();
					long tmp = fileSize1 - fileSize2;
					if ( tmp == 0 ) {
						result = file1.getName().compareToIgnoreCase(file2.getName());
					} else if ( tmp > 0 ) {
						result = 1;
					} else if ( tmp < 0 ) {
						result = -1;
					}
				}
			}
			return result;
		}
	};

	public static class RemoteFileSizeDescComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= RemoteFile.FileType.REMOTE_MAC_ALIAS_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					long fileSize1 = file1.getSize();
					long fileSize2 = file2.getSize();
					long tmp = fileSize2 - fileSize1;
					if ( tmp == 0 ) {
						result = file2.getName().compareToIgnoreCase(file1.getName());
					} else if ( tmp > 0 ) {
						result = 1;
					} else if ( tmp < 0 ) {
						result = -1;
					}
				}
			}
			return result;
		}
	};

	public static class RemoteFileTypeAscComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= RemoteFile.FileType.REMOTE_MAC_ALIAS_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					String extension1 = file1.getExtension();
					String extension2 = file2.getExtension();
					if ( extension1 == null && extension2 == null ) {
						result = 0;
					} else {
						result = extension1.compareToIgnoreCase(extension2);
					}
					if ( result == 0 ) {
						result = file1.getName().compareToIgnoreCase(file2.getName());
					}
				}
			}
			return result;
		}
	};

	public static class RemoteFileTypeDescComparator implements Comparator<RemoteFile> {
		@Override
		public int compare(RemoteFile file1, RemoteFile file2) {
			int result = file1.getType().getSortIndex() - file2.getType().getSortIndex();
			if ( result == 0 ) {
				if ( file1.getType().getSortIndex() <= RemoteFile.FileType.REMOTE_MAC_ALIAS_DIR.getSortIndex() ) {
					result = file1.getName().compareToIgnoreCase(file2.getName());
				} else {
					String extension1 = file1.getExtension();
					String extension2 = file2.getExtension();
					if ( extension1 == null && extension2 == null ) {
						result = 0;
					} else {
						result = extension2.compareToIgnoreCase(extension1);
					}
					if ( result == 0 ) {
						result = file2.getName().compareToIgnoreCase(file1.getName());
					}
				}
			}
			return result;
		}
	};

}
