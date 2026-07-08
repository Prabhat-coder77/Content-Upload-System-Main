
import { useState } from "react";
import { Download, File, ImageIcon, FileText, Video, FileType2, Pencil, Trash2, Check, X } from "lucide-react";
import { formatBytes, formatDate } from "../utils/format";

interface ContentMetadata {
    id: string;
    name: string;
    type: string;
    size: number;
    uploadDate: string;
}

interface ContentTableProps {
    contents: ContentMetadata[];
    isLoading: boolean;
    onDownload: (id: string, name: string) => void;
    onDelete: (id: string) => Promise<void>;
    onRename: (id: string, newName: string) => Promise<void>;
}

export function ContentTable({ contents, isLoading, onDownload, onDelete, onRename }: ContentTableProps) {
    const [editingId, setEditingId] = useState<string | null>(null);
    const [editName, setEditName] = useState("");
    const [deletingId, setDeletingId] = useState<string | null>(null);
    const [savingId, setSavingId] = useState<string | null>(null);

    const getFileIcon = (type: string) => {
        if (type.includes("pdf")) return <FileText className="h-5 w-5 text-danger" />;
        if (type.includes("image")) return <ImageIcon className="h-5 w-5 text-primary" />;
        if (type.includes("video")) return <Video className="h-5 w-5 text-purple-500" />;
        return <File className="h-5 w-5 text-text-muted" />;
    };

    const startEdit = (item: ContentMetadata) => {
        setEditingId(item.id);
        setEditName(item.name);
    };

    const cancelEdit = () => {
        setEditingId(null);
        setEditName("");
    };

    const saveEdit = async (id: string) => {
        if (!editName.trim()) return;
        setSavingId(id);
        try {
            await onRename(id, editName.trim());
        } finally {
            setSavingId(null);
            setEditingId(null);
            setEditName("");
        }
    };

    const handleDelete = async (id: string) => {
        setDeletingId(id);
        try {
            await onDelete(id);
        } finally {
            setDeletingId(null);
        }
    };

    if (isLoading) {
        return (
            <div className="animate-pulse flex flex-col space-y-3">
                {[...Array(5)].map((_, i) => (
                    <div key={i} className="h-16 bg-white/5 rounded-xl w-full"></div>
                ))}
            </div>
        );
    }

    if (contents.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center p-16 text-center border border-border border-dashed rounded-xl bg-white/5">
                <div className="h-16 w-16 rounded-2xl bg-[#1a1a1a] flex items-center justify-center mb-4 shadow-sm border border-border">
                    <FileType2 className="h-8 w-8 text-text-muted" />
                </div>
                <h3 className="font-bold text-lg text-text-main tracking-tight">No content uploaded yet</h3>
                <p className="text-sm text-text-muted mt-2 max-w-[280px] font-medium leading-relaxed">
                    Upload your first course file using the panel on the left to see it listed here.
                </p>
            </div>
        );
    }

    return (
        <div className="bg-surface rounded-xl border border-border shadow-sm overflow-hidden flex-1 flex flex-col">
            <div className="overflow-x-auto">
                <table className="w-full text-sm text-left">
                    <thead className="text-xs text-text-muted uppercase bg-white/5 border-b border-border">
                        <tr>
                            <th scope="col" className="px-6 py-4 font-bold tracking-wider">File Details</th>
                            <th scope="col" className="px-6 py-4 font-bold tracking-wider whitespace-nowrap">Size</th>
                            <th scope="col" className="px-6 py-4 font-bold tracking-wider hidden md:table-cell whitespace-nowrap">Uploaded On</th>
                            {/* Fixed width so it never squeezes the file name column */}
                            <th scope="col" className="px-4 py-4 font-bold tracking-wider text-right w-px whitespace-nowrap">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {contents.map((item) => (
                            <tr key={item.id} className="bg-transparent border-b border-border/50 hover:bg-white/5 transition-colors last:border-0 group">
                                {/* File Details */}
                                <td className="px-6 py-4">
                                    <div className="flex items-center gap-4">
                                        <div className="p-2.5 rounded-xl bg-background border border-border/60 shadow-sm group-hover:scale-105 transition-transform group-hover:border-border shrink-0">
                                            {getFileIcon(item.type)}
                                        </div>
                                        <div className="min-w-0">
                                            {editingId === item.id ? (
                                                <div className="flex items-center gap-2">
                                                    <input
                                                        autoFocus
                                                        value={editName}
                                                        onChange={(e) => setEditName(e.target.value)}
                                                        onKeyDown={(e) => {
                                                            if (e.key === "Enter") saveEdit(item.id);
                                                            if (e.key === "Escape") cancelEdit();
                                                        }}
                                                        className="w-40 bg-background border border-primary rounded-md px-2 py-1 text-sm text-text-main focus:outline-none focus:ring-2 focus:ring-primary"
                                                    />
                                                    <button
                                                        onClick={() => saveEdit(item.id)}
                                                        disabled={savingId === item.id}
                                                        className="p-1 rounded text-green-400 hover:bg-green-400/10 disabled:opacity-50"
                                                        title="Save"
                                                    >
                                                        <Check className="h-4 w-4" />
                                                    </button>
                                                    <button
                                                        onClick={cancelEdit}
                                                        className="p-1 rounded text-text-muted hover:bg-white/10"
                                                        title="Cancel"
                                                    >
                                                        <X className="h-4 w-4" />
                                                    </button>
                                                </div>
                                            ) : (
                                                <>
                                                    <div className="font-semibold text-text-main max-w-[130px] lg:max-w-[200px] truncate" title={item.name}>
                                                        {item.name}
                                                    </div>
                                                    <div className="text-xs text-text-muted font-medium mt-0.5 uppercase tracking-wider">
                                                        {item.type.split('/')[1] || 'Unknown'}
                                                    </div>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                </td>

                                {/* Size */}
                                <td className="px-6 py-4 whitespace-nowrap text-text-muted font-medium">
                                    {formatBytes(item.size)}
                                </td>

                                {/* Upload Date */}
                                <td className="px-6 py-4 whitespace-nowrap text-text-muted font-medium hidden md:table-cell">
                                    {formatDate(item.uploadDate)}
                                </td>

                                {/* Actions — icon-only so all 3 buttons always fit */}
                                <td className="px-4 py-4 whitespace-nowrap w-px">
                                    {editingId !== item.id && (
                                        <div className="flex items-center justify-end gap-1">
                                            <button
                                                onClick={() => startEdit(item)}
                                                title="Rename"
                                                className="p-2 rounded-lg text-text-muted hover:text-primary hover:bg-primary/10 transition-colors"
                                            >
                                                <Pencil className="h-4 w-4" />
                                            </button>
                                            <button
                                                onClick={() => onDownload(item.id, item.name)}
                                                title="Download"
                                                className="p-2 rounded-lg text-text-muted hover:text-primary hover:bg-primary/10 transition-colors"
                                            >
                                                <Download className="h-4 w-4" />
                                            </button>
                                            <button
                                                onClick={() => handleDelete(item.id)}
                                                disabled={deletingId === item.id}
                                                title="Delete"
                                                className="p-2 rounded-lg text-text-muted hover:text-danger hover:bg-danger/10 transition-colors disabled:opacity-50"
                                            >
                                                {deletingId === item.id ? (
                                                    <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                                                    </svg>
                                                ) : (
                                                    <Trash2 className="h-4 w-4" />
                                                )}
                                            </button>
                                        </div>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
